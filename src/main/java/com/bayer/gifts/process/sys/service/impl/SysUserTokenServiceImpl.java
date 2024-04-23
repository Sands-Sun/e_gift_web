package com.bayer.gifts.process.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bayer.gifts.process.config.ManageConfig;
import com.bayer.gifts.process.dao.SysUserTokenDao;

import com.bayer.gifts.process.sys.auth2.TokenGenerator;
import com.bayer.gifts.process.sys.entity.SysUserTokenEntity;
import com.bayer.gifts.process.sys.service.SysUserTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service("sysUserTokenService")
public class SysUserTokenServiceImpl extends ServiceImpl<SysUserTokenDao, SysUserTokenEntity> implements SysUserTokenService {



    //1小时后过期
//    private final static int EXPIRE = 3600 ;

    @Value("${manage.tokenExpireTime}")
    private int tokenExpireTime;

    public SysUserTokenEntity createUserToken(Long userId) {
        String token = TokenGenerator.generateValue();
        SysUserTokenEntity tokenEntity =
                this.baseMapper.selectOne(Wrappers.<SysUserTokenEntity>lambdaQuery()
                        .eq(SysUserTokenEntity::getUserId, userId));
        //当前时间
        Date now = new Date();
        //过期时间
        Date expireTime = new Date(now.getTime() + tokenExpireTime  * 1000L);
        if(Objects.isNull(tokenEntity)){
            tokenEntity = new SysUserTokenEntity();
            tokenEntity.setState(UUID.randomUUID().toString());
            tokenEntity.setUserId(userId);
            tokenEntity.setToken(token);
            tokenEntity.setCreateTime(now);
            tokenEntity.setExpireTime(expireTime);
            this.baseMapper.insert(tokenEntity);
        }else {
            tokenEntity.setToken(token);
            tokenEntity.setExpireTime(expireTime);
            this.baseMapper.update(tokenEntity,new UpdateWrapper<SysUserTokenEntity>()
                    .eq("user_id", userId));
        }
        return tokenEntity;
    }
}
