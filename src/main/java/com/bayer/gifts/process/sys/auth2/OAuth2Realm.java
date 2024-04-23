package com.bayer.gifts.process.sys.auth2;


import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.sys.entity.SysUserTokenEntity;
import com.bayer.gifts.process.sys.service.ShiroService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 认证
 *
 * @author wangym
 */
@Component
public class OAuth2Realm extends AuthorizingRealm {
    @Autowired
    ShiroService shiroService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    /**
     * 授权(验证权限时调用)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        UserExtensionEntity user = (UserExtensionEntity)principals.getPrimaryPrincipal();
//        Long userId = user.getSfUserId();
//        List<MDGiftsGroupEntity> groups = giftsGroupDao.queryGroupListByUserId(userId);
//        if(CollectionUtils.isNotEmpty(groups)) {
//            user.setGroups(groups);
//        }
        //用户权限列表
        //Set<String> permsSet = shiroService.getUserPermissions(userId);

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //info.setStringPermissions(permsSet);
        return info;
    }

    /**
     * 认证(登录时调用)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String accessToken = (String) token.getPrincipal();

        //根据accessToken，查询用户信息
        List<SysUserTokenEntity> tokenEntity = shiroService.queryByToken(accessToken);

        //token失效
        if(tokenEntity == null || tokenEntity.isEmpty() || tokenEntity.get(0).getExpireTime().getTime() < System.currentTimeMillis()){
            throw new IncorrectCredentialsException("token invalid, please log in again");
        }

        //查询用户信息
        UserExtensionEntity user = shiroService.queryUser(tokenEntity.get(0).getUserId());
        user.fillInDivision();
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, accessToken, getName());
        return info;
    }
}
