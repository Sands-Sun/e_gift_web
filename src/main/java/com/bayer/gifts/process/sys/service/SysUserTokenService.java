package com.bayer.gifts.process.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bayer.gifts.process.sys.entity.SysUserTokenEntity;

public interface SysUserTokenService extends IService<SysUserTokenEntity> {

    SysUserTokenEntity createUserToken(Long userId);
}
