package com.bayer.gifts.process.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.service.UserInfoService;
import com.bayer.gifts.process.sys.entity.SysUserTokenEntity;
import com.bayer.gifts.process.sys.service.ShiroService;
import com.bayer.gifts.process.sys.service.SysUserTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;



@Slf4j
@Service("shiroService")
public class ShiroServiceImpl implements ShiroService {

    @Autowired
    SysUserTokenService sysUserTokenService;

    @Lazy
    @Autowired
    UserInfoService userInfoService;


    @Override
    public List<SysUserTokenEntity> queryByToken(String token) {
        return sysUserTokenService.list(new QueryWrapper<SysUserTokenEntity>().eq("TOKEN", token));
    }

    @Override
    public UserExtensionEntity queryUser(Long userId) {
        return userInfoService.getUserInfo(userId,false,true,true);
    }
}
