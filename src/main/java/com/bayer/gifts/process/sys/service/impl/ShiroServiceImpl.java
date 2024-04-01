package com.bayer.gifts.process.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bayer.gifts.process.dao.GiftsGroupDao;
import com.bayer.gifts.process.dao.GiftsRoleDao;
import com.bayer.gifts.process.dao.SysUserTokenDao;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.service.GiftsGroupService;
import com.bayer.gifts.process.service.UserInfoService;
import com.bayer.gifts.process.sys.entity.SysUserTokenEntity;
import com.bayer.gifts.process.sys.service.ShiroService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;



@Slf4j
@Service("shiroService")
public class ShiroServiceImpl implements ShiroService {

    @Autowired
    SysUserTokenDao sysUserTokenDao;

    @Lazy
    @Autowired
    UserInfoService userInfoService;

    @Lazy
    @Autowired
    GiftsGroupService giftsGroupService;

    @Override
    public List<SysUserTokenEntity> queryByToken(String token) {
        return sysUserTokenDao.selectList(new QueryWrapper<SysUserTokenEntity>().eq("TOKEN", token));
    }

    @Override
    public UserExtensionEntity queryUser(Long userId) {
        UserExtensionEntity user = userInfoService.getById(userId);
        log.info("UserId: {} CWID: {}", user.getSfUserId(), user.getCwid());
        Long supervisorId = user.getSupervisorId();
        UserExtensionEntity supervisor = userInfoService.getById(supervisorId);
        if(Objects.nonNull(supervisor)){
            log.info("Supervisor ---> UserId: {} CWID: {}", supervisor.getSfUserId(), supervisor.getCwid());
            user.setSupervisor(supervisor);
        }
        List<GiftsGroupEntity> groups = giftsGroupService.getGroupListByUserId(userId);
        if(CollectionUtils.isNotEmpty(groups)) {
            log.info("Group size: {}", groups.size());
            user.setGroups(groups);
        }
        return user;
    }
}
