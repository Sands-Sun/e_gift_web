package com.bayer.gifts.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.dao.UserExtensionDao;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.param.UserParam;
import com.bayer.gifts.process.param.UserSearchParam;
import com.bayer.gifts.process.service.GiftsGroupService;
import com.bayer.gifts.process.service.UserInfoService;
import com.bayer.gifts.process.sys.entity.SysUserTokenEntity;
import com.bayer.gifts.process.sys.service.SysUserTokenService;
import com.bayer.gifts.process.utils.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service("userInfoService")
public class UserInfoServiceImpl extends ServiceImpl<UserExtensionDao, UserExtensionEntity> implements UserInfoService{


    @Autowired
    UserExtensionDao userExtensionDao;

    @Autowired
    GiftsGroupService giftsGroupService;

    @Autowired
    SysUserTokenService tokenService;

    @Override
    public Pagination<UserExtensionEntity> getUserList(UserParam param) {
        log.info("get user page...");
        Page<UserExtensionEntity> pagination = new Page<>(param.getCurrentPage(), param.getPageSize());
        pagination.setSearchCount(false);
        long totalCount = userExtensionDao.queryUserCount(param);
        IPage<UserExtensionEntity> page = userExtensionDao.queryUserList(pagination,param);
        page.setTotal(totalCount);
        return new Pagination<>(page);
    }


    @Override
    public List<UserExtensionEntity> searchUserList(boolean baseOnCompany,UserSearchParam searchParam) {
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        if (baseOnCompany) {
            searchParam.setCompanyCode(user.getCompanyCode());
        }
        List<UserExtensionEntity> users = userExtensionDao.queryFuzzyUserList(searchParam);
        log.info("user list size: {}", users.size());
        return users;
    }

    @Override
    public UserExtensionEntity getUserInfo(Long userId, boolean includeRole,boolean includeGroup) {
        UserExtensionEntity user = this.getById(userId);
        log.info("UserId: {} CWID: {}", user.getSfUserId(), user.getCwid());
        Long supervisorId = user.getSupervisorId();
        UserExtensionEntity supervisor = this.getById(supervisorId);
        if(Objects.nonNull(supervisor)){
            log.info("Supervisor ---> UserId: {} CWID: {}", supervisor.getSfUserId(), supervisor.getCwid());
            user.setSupervisor(supervisor);
        }
        if(includeGroup) {
            List<GiftsGroupEntity> groups = giftsGroupService.getGroupListByUserId(userId);
            if(CollectionUtils.isNotEmpty(groups)) {
                log.info("Group size: {}", groups.size());
                user.setGroups(groups);
            }
        }
        if(includeRole) {
            log.info("add role information...");
        }
        return user;
    }

    @Override
    public UserExtensionEntity getUserInfoByToken(String token) {
        Date currentDate = new Date();
        SysUserTokenEntity userToken = tokenService.getOne(Wrappers.<SysUserTokenEntity>lambdaQuery()
                .eq(SysUserTokenEntity::getToken,token));
        if(Objects.isNull(userToken)) {
            return null;
        }
        if(userToken.getExpireTime().before(currentDate)){
            return null;
        }
        UserExtensionEntity user = getUserInfo(userToken.getUserId(), false,false);
        log.info("user information: {}", user);
        user.fillInDivision();
        return user;
    }
}
