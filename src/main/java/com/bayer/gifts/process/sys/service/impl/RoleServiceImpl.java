package com.bayer.gifts.process.sys.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.MasterTransactional;
import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.service.UserInfoService;
import com.bayer.gifts.process.sys.dao.RoleDao;
import com.bayer.gifts.process.sys.entity.RoleEntity;
import com.bayer.gifts.process.sys.entity.UserToRoleEntity;
import com.bayer.gifts.process.sys.param.RoleParam;
import com.bayer.gifts.process.sys.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleDao roleDao;

    @Autowired
    UserInfoService userInfoService;

    @Override
    public Pagination<RoleEntity> getAllRoles(RoleParam param) {
        IPage<RoleEntity> pageInfo= roleDao.selectAll(new Page<>(param.getCurrentPage(), param.getPageSize()), param);
        fillInBindUser(pageInfo);
        return new Pagination<>(pageInfo);
    }


    private void fillInBindUser(IPage<RoleEntity> pageInfo) {
        log.info("fill in bind user...");
        List<RoleEntity> roles = pageInfo.getRecords();
        List<Long> roleIds =  roles.stream().map(RoleEntity::getId).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(roleIds)){
            List<UserToRoleEntity> userToRoles = roleDao.queryUserToRoleList(roleIds);
            if(CollectionUtils.isNotEmpty(userToRoles)){
                Map<Long, List<UserToRoleEntity>> userToRoleGroups = userToRoles.stream()
                        .collect(Collectors.groupingBy(UserToRoleEntity::getRoleId));
                for(RoleEntity role : roles){
                    Long roleId = role.getId();
                    List<UserToRoleEntity> userToRole =userToRoleGroups.getOrDefault(roleId, null);
                    if(CollectionUtils.isNotEmpty(userToRole)){
                        role.setUserToRoles(userToRole);
                    }
                }
            }
        }
    }

    @Override
    @MasterTransactional
    public void saveRole(RoleParam param) {
        RoleEntity role = new RoleEntity();
        Date currentDate = new Date();
        BeanUtils.copyProperties(param, role);
        if(Objects.nonNull(param.getId())){
            role.setId(param.getId());
//            role.setMarkDeleted(param.getStatus());
            role.setLastModifiedBy(param.getUserId());
            role.setLastModifiedDate(currentDate);
            roleDao.updateById(role);
            saveOrUpdateUserToRole(role, param);
        }else {
            role.setCreatedDate(currentDate);
            role.setCreatedBy(param.getUserId());
            role.setLastModifiedDate(currentDate);
            role.setLastModifiedBy(param.getUserId());
//            role.setMarkDeleted(param.getStatus());
            roleDao.insert(role);
            saveOrUpdateUserToRole(role, param);
        }
    }

    @Override
    @MasterTransactional
    public void deleteRoleByIds(List<String> ids) {
        roleDao.batchDeleteUserToRoleByRoleIds(ids);
        roleDao.deleteBatchIds(ids);

    }


    public void saveOrUpdateUserToRole(RoleEntity roleEntity, RoleParam param) {
        if(CollectionUtils.isEmpty(param.getUserEmails())){
            log.info("empty user email...");
            return;
        }
        List<UserExtensionEntity> users = userInfoService.list(Wrappers.<UserExtensionEntity>lambdaQuery().
                in(UserExtensionEntity::getEmail, param.getUserEmails()).
                eq(UserExtensionEntity::getMarkDeleted, Constant.NO_EXIST_MARK));
        if(CollectionUtils.isEmpty(users)){
            log.info("empty user list...");
            return;
        }
        log.info("delete user to role by roleId : {}", roleEntity.getId());
        roleDao.deleteUserToRoleByRoleId(roleEntity.getId());
        List<UserToRoleEntity> userToRoles = users.stream().map(user -> {
            UserToRoleEntity userToRole = new UserToRoleEntity();
            BeanUtils.copyProperties(roleEntity,userToRole);
            userToRole.setRoleId(roleEntity.getId());
            userToRole.setUserId(user.getSfUserId());
            return userToRole;
        }).collect(Collectors.toList());
        this.roleDao.batchInsertUserToRole(userToRoles);
    }
}
