/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.bayer.gifts.process.sys.service;


import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.sys.entity.SysUserTokenEntity;

import java.util.List;

/**
 * shiro相关接口
 */
public interface ShiroService {


    List<SysUserTokenEntity> queryByToken(String token);

    /**
     * 根据用户ID，查询用户
     * @param userId
     */
    UserExtensionEntity queryUser(Long userId);
}
