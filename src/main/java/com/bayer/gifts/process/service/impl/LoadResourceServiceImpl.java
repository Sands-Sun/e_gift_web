package com.bayer.gifts.process.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.dao.GiftsDictionaryDao;
import com.bayer.gifts.process.entity.GiftsDictionaryEntity;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.mail.dao.MailTemplateDao;
import com.bayer.gifts.process.mail.entity.MailPolicy;
import com.bayer.gifts.process.mail.entity.MailTemplate;
import com.bayer.gifts.process.service.GiftsGroupService;
import com.bayer.gifts.process.service.LoadResourceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("loadResourceService")
public class LoadResourceServiceImpl implements LoadResourceService {


    @Autowired
    MailTemplateDao mailTemplateDao;

    @Autowired
    GiftsDictionaryDao giftsDictionaryDao;

    @Autowired
    GiftsGroupService giftsGroupService;

    @Override
    public void load() {
        loadMailPolicy();
        loadGiftsGroup();
        loadGiftsDictionary();
    }

    @Override
    public void refreshDisableSentMail(Date refreshDate) {

    }

    @Override
    public void refreshGiftGroup(String groupId) {
        GiftsGroupEntity group = giftsGroupService.getGiftsGroupById(groupId);
        if(Objects.nonNull(group)){
            String groupCode = group.getGroupCode();
            log.info("Refresh gift group code: {}", groupCode);
            Constant.GIFTS_GROUP_MAP.put(groupCode,group);
        }
    }
    @Override
    public void loadGiftsDictionary() {
        log.info("Load Gifts Dictionary...");
        List<GiftsDictionaryEntity> dicts = giftsDictionaryDao.selectList(Wrappers.<GiftsDictionaryEntity>lambdaQuery()
                .eq(GiftsDictionaryEntity::getMarkDeleted, Constant.NO_EXIST_MARK));
        if(CollectionUtils.isNotEmpty(dicts)){
            Constant.GIFTS_DICT_MAP = dicts.stream()
                    .collect(Collectors.groupingBy(d -> Pair.of(d.getCategory(), d.getLanguage())));
        }
    }
    @Override
    public void loadGiftsGroup() {
        log.info("Load Gifts Group...");
        List<GiftsGroupEntity> groups = giftsGroupService.getAllGroupList();
        if(CollectionUtils.isNotEmpty(groups)){
            Constant.GIFTS_GROUP_MAP = groups.stream().collect(Collectors.toMap(GiftsGroupEntity::getGroupCode, g->g,
                    (oldValue, newValue) -> newValue));
        }
    }

    @Override
    public void loadMailPolicy() {
        log.info("Load Mail Template...");
        List<MailTemplate> mailTemplateList = mailTemplateDao.selectList(Wrappers.<MailTemplate>lambdaQuery()
                .eq(MailTemplate::getMarkDeleted, Constant.NO_EXIST_MARK));
        List<Long> templateIds = mailTemplateList.stream()
                .map(MailTemplate::getId).collect(Collectors.toList());
        log.info("Mail Template size: {}", templateIds);
        List<MailPolicy>  mailPolicyList = mailTemplateDao.selectMailPolicyList(templateIds);
        Map<Long, List<MailPolicy>>  mailPolicyMap = mailPolicyList.stream().
                collect(Collectors.groupingBy(MailPolicy::getTemplateId));
        mailTemplateList.forEach(mailTemplate -> {
            Long mailTemplateId = mailTemplate.getId();
            if(mailPolicyMap.containsKey(mailTemplateId)){
                mailTemplate.setPolicyList(mailPolicyMap.get(mailTemplateId));
            }
        });
        Map<String, List<MailTemplate>> mailTemplateMap = mailTemplateList.stream()
                .collect(Collectors.groupingBy(MailTemplate::getProcessType));
        mailTemplateMap.forEach((k,v) -> {
            Map<String, MailTemplate> policyMap = v.stream().collect(
                    Collectors.toMap(MailTemplate::getMailType, m -> m,
                    (oldValue, newValue) -> newValue));
            log.info("Template processType: {}, size: {}", k,v.size());
            Constant.MAIL_TEMPLATE_MAP.put(k,policyMap);
        });
    }

}
