package com.bayer.gifts.process.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bayer.gifts.process.sys.entity.SysUserTokenEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SysUserTokenService extends IService<SysUserTokenEntity> {

    SysUserTokenEntity createUserToken(Long userId);

    String saveAzureToken(HttpServletRequest request, HttpServletResponse response);
}
