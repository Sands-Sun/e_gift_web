package com.bayer.gifts.process.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bayer.gifts.process.dao.UserExtensionDao;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.param.UserSearchParam;
import com.bayer.gifts.process.service.GiftsGroupService;
import com.bayer.gifts.process.service.UserInfoService;
import com.bayer.gifts.process.utils.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service("userInfoService")
public class UserInfoServiceImpl extends ServiceImpl<UserExtensionDao, UserExtensionEntity> implements UserInfoService{


    @Autowired
    UserExtensionDao userExtensionDao;

    @Autowired
    GiftsGroupService giftsGroupService;

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
}
