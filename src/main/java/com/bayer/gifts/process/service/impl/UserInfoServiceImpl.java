package com.bayer.gifts.process.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bayer.gifts.process.dao.UserExtensionDao;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("userInfoService")
public class UserInfoServiceImpl extends ServiceImpl<UserExtensionDao, UserExtensionEntity> implements UserInfoService{



    @Override
    public UserExtensionEntity getUserInfo(Long userId, boolean includeRole) {
        UserExtensionEntity user = this.getById(userId);
        if(includeRole) {
            log.info("add role information...");
        }
        return user;
    }
}
