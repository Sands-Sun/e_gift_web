package com.bayer.gifts.process.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.bayer.gifts.process.dao.UserExtensionDao;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.sys.auth2.AuthHelper;
import com.bayer.gifts.process.sys.auth2.TokenGenerator;
import com.bayer.gifts.process.sys.dao.SysUserTokenDao;
import com.bayer.gifts.process.sys.entity.SysUserTokenEntity;
import com.bayer.gifts.process.sys.service.SysUserTokenService;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Service("sysUserTokenService")
public class SysUserTokenServiceImpl extends ServiceImpl<SysUserTokenDao, SysUserTokenEntity> implements SysUserTokenService {



    @Autowired
    SysUserTokenDao sysUserTokenDao;
    @Autowired
    AuthHelper authHelper;
    @Autowired
    UserExtensionDao userExtensionDao;
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


    @Override
    public String saveAzureToken(HttpServletRequest request, HttpServletResponse response) {

        String token="";
        String currentUri = request.getRequestURL().toString();
        String queryStr = request.getQueryString();
        String fullUrl = currentUri + (queryStr != null ? "?" + queryStr : "");

        if(containsAuthenticationCode(request)) {
            // response should have authentication code, which will be used to acquire access token
            try {

                IAuthenticationResult result = authHelper.processAuthenticationCodeRedirect(request, currentUri, fullUrl);

                String userEmail = result.account().username();
                UserExtensionEntity userInfoEntity = userExtensionDao.selectOne(new QueryWrapper<UserExtensionEntity>().eq("EMAIL", userEmail).eq("mark_deleted","N"));

                if(userInfoEntity==null ||userInfoEntity.getSfUserId()==null){
                    //用户不在数据库中
                }else {
                    Long userId = userInfoEntity.getSfUserId();
                    token = TokenGenerator.generateValue();
                    SysUserTokenEntity tokenEntity =
                            sysUserTokenDao.selectOne(Wrappers.<SysUserTokenEntity>lambdaQuery()
                                    .eq(SysUserTokenEntity::getUserId, userId));
                    //当前时间
                    Date now = new Date();
                    //过期时间
                    Date expireTime = new Date(now.getTime() + tokenExpireTime * 1000);
                    if(Objects.isNull(tokenEntity)){
                        tokenEntity = new SysUserTokenEntity();
                        tokenEntity.setUserId(userId);
                        tokenEntity.setToken(token);
                        tokenEntity.setCreateTime(now);
                        tokenEntity.setExpireTime(expireTime);
                        sysUserTokenDao.insert(tokenEntity);
                    }else {
                        tokenEntity.setToken(token);
                        tokenEntity.setExpireTime(expireTime);
                        sysUserTokenDao.update(tokenEntity,new UpdateWrapper<SysUserTokenEntity>()
                                .eq("user_id", userId));
                    }

                }

            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }else {
            //获取token失败
        }

        return token;
    }

    private boolean containsAuthenticationCode(HttpServletRequest httpRequest) {
        Map<String, String[]> httpParameters = httpRequest.getParameterMap();

        boolean isPostRequest = httpRequest.getMethod().equalsIgnoreCase("POST");
        boolean containsErrorData = httpParameters.containsKey("error");
        boolean containIdToken = httpParameters.containsKey("id_token");
        boolean containsCode = httpParameters.containsKey("code");

        return isPostRequest && containsErrorData || containsCode || containIdToken;
    }

}
