package com.bayer.gifts.process.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.dao.GiftsGroupDao;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.entity.GiftsUserToGroupEntity;
import com.bayer.gifts.process.mail.dao.CommonMailPolicyDao;
import com.bayer.gifts.process.mail.entity.CommonMailPolicy;
import com.bayer.gifts.process.service.GiftsGroupService;
import com.bayer.gifts.process.service.LoadResourceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("loadResourceService")
public class LoadResourceServiceImpl implements LoadResourceService {


    @Autowired
    CommonMailPolicyDao commonMailPolicyDao;

    @Autowired
    GiftsGroupService giftsGroupService;

    @Override
    public void load() {
        loadMailPolicy();
        loadGiftsGroup();
    }

    @Override
    public void refreshDisableSentMail(Date refreshDate) {

    }

    @Override
    public void refreshGiftGroup(Long groupId) {
        GiftsGroupEntity group = giftsGroupService.getGiftsGroupById(groupId);
        if(Objects.nonNull(group)){
            String groupCode = group.getGroupCode();
            log.info("Refresh gift group code: {}", groupCode);
            Constant.GIFTS_GROUP_MAP.put(groupCode,group);
        }
    }

    private void loadGiftsGroup() {
        log.info("Load Gifts Group...");
        List<GiftsGroupEntity> groups = giftsGroupService.getAllGroupList();
        if(CollectionUtils.isNotEmpty(groups)){
            Constant.GIFTS_GROUP_MAP = groups.stream().collect(Collectors.toMap(GiftsGroupEntity::getGroupCode, g->g,
                    (oldValue, newValue) -> newValue));
        }
    }


    private void loadMailPolicy() {
        log.info("Load Mail Policy...");
        List<CommonMailPolicy> commonMailPolicyList = commonMailPolicyDao.selectList(Wrappers.<CommonMailPolicy>lambdaQuery()
                .eq(CommonMailPolicy::getMarkDeleted, Constant.EXIST_MARK));
        Map<String, List<CommonMailPolicy>> commonMailPolicyMap = commonMailPolicyList.stream()
                .collect(Collectors.groupingBy(CommonMailPolicy::getProcessType));

        commonMailPolicyMap.forEach((k,v) -> {
            Map<String, CommonMailPolicy> policyMap = v.stream().collect(Collectors.toMap(CommonMailPolicy::getMailType, m -> m,
                    (oldValue, newValue) -> newValue));
            log.info("Policy processType: {}, size: {}", k,v.size());
            Constant.MAIL_POLICY_MAP.put(k,policyMap);
        });
    }

}
