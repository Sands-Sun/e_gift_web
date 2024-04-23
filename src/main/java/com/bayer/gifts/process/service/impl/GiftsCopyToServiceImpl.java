package com.bayer.gifts.process.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bayer.gifts.process.dao.GiftsCopyToDao;
import com.bayer.gifts.process.entity.GiftsCopyToEntity;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.form.GivingGiftsForm;
import com.bayer.gifts.process.service.GiftsCopyToService;
import com.bayer.gifts.process.service.UserInfoService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service("giftsCopyToService")
public class GiftsCopyToServiceImpl extends ServiceImpl<GiftsCopyToDao, GiftsCopyToEntity> implements GiftsCopyToService {


    @Autowired
    UserInfoService userInfoService;
    @Override
    public List<GiftsCopyToEntity> getGiftsCopyToList(Long applicationId, String type) {
        return this.baseMapper.queryGiftsCopyToList(applicationId,type);
    };

    @Override
    public List<GiftsCopyToEntity> saveOrUpdateGiftsCopyTo(Long applicationId,String type, List<String> userEmails,UserExtensionEntity user) {
        log.info("save giving gifts copyTo...");
        if(CollectionUtils.isEmpty(userEmails)){
            log.info("empty copyTo user ids...");
            return Collections.emptyList();
        }
        List<UserExtensionEntity> copyToList = userInfoService.list(Wrappers.<UserExtensionEntity>lambdaQuery().
                in(UserExtensionEntity::getEmail, userEmails));
        if(CollectionUtils.isEmpty(copyToList)){
            log.info("empty copyTo user list...");
            return Collections.emptyList();
        }
        this.baseMapper.deleteByApplicationId(applicationId);
        List<GiftsCopyToEntity> copyToEntityList = Lists.newArrayList();
        GiftsCopyToEntity copyToEntity;
        for(UserExtensionEntity copyTo : copyToList){
            copyToEntity = new GiftsCopyToEntity();
            //"Giving"
            copyToEntity.setType(type);
            copyToEntity.setApplicationId(applicationId);
            copyToEntity.setSfUserIdFrom(user.getSfUserId());
            copyToEntity.setSfUserIdCopyTo(copyTo.getSfUserId());
            copyToEntity.setCopytoCwid(copyTo.getCwid());
            copyToEntity.setCopytoFirstName(copyTo.getFirstName());
            copyToEntity.setCopytoLastName(copyTo.getLastName());
            copyToEntityList.add(copyToEntity);
            this.baseMapper.insert(copyToEntity);
        }
        return  copyToEntityList;
    }

}
