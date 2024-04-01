package com.bayer.gifts.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bayer.gifts.process.entity.UserExtensionEntity;

public interface UserInfoService extends IService<UserExtensionEntity> {

    UserExtensionEntity getUserInfo(Long userId, boolean includeRole);
}
