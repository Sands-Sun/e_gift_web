package com.bayer.gifts.process.sys.service;

import com.bayer.gifts.process.sys.entity.SysUserTokenEntity;

public interface SysUserTokenService {

    SysUserTokenEntity createUserToken(Long userId);
}
