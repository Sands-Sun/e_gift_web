package com.bayer.gifts.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bayer.gifts.process.entity.GiftsCopyToEntity;
import com.bayer.gifts.process.entity.UserExtensionEntity;

import java.util.List;

public interface GiftsCopyToService extends IService<GiftsCopyToEntity> {

    List<GiftsCopyToEntity> saveOrUpdateGiftsCopyTo(Long applicationId,String type, List<String> userEmails, UserExtensionEntity user);
}
