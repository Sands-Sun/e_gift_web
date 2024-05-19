package com.bayer.gifts.process.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.MasterTransactional;
import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.common.validator.group.Group;
import com.bayer.gifts.process.dao.GiftsGroupDao;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.entity.GiftsUserToGroupEntity;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.param.GiftsGroupParam;
import com.bayer.gifts.process.service.GiftsGroupService;
import com.bayer.gifts.process.service.LoadResourceService;
import com.bayer.gifts.process.service.UserInfoService;
import com.bayer.gifts.process.utils.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("giftsGroupService")
public class GiftsGroupServiceImpl extends ServiceImpl<GiftsGroupDao, GiftsGroupEntity> implements GiftsGroupService {

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    LoadResourceService loadResourceService;

    @Override
    @MasterTransactional
    public void saveGiftsGroup(GiftsGroupParam param) {
        log.info("save gifts group...");
       int count = this.baseMapper.selectCount(Wrappers.<GiftsGroupEntity>lambdaQuery()
               .eq(GiftsGroupEntity::getGroupCode,param.getGroupCode()));
       if(count > 0 ){
           log.info("exist gifts group no need save...");
           return;
       }
        Date currentDate = new Date();
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        GiftsGroupEntity giftsGroup = new GiftsGroupEntity();
        BeanUtils.copyProperties(param,giftsGroup);
        giftsGroup.setMarkDeleted(param.getMarkDeleted());
        giftsGroup.setCreatedBy(user.getSfUserId());
        giftsGroup.setCreatedDate(currentDate);
        giftsGroup.setLastModifiedBy(user.getSfUserId());
        giftsGroup.setLastModifiedDate(currentDate);
        this.baseMapper.insert(giftsGroup);
        log.info("save gifts user to group...");
        this.saveOrUpdateUserToGroup(giftsGroup,param);
        loadResourceService.refreshGiftGroup(giftsGroup.getId());
    }


    @Override
    @MasterTransactional
    public void updateGiftsGroup(GiftsGroupParam param){
        log.info("update gifts group...");
        GiftsGroupEntity history = this.baseMapper.selectOne(Wrappers.<GiftsGroupEntity>lambdaQuery()
                .eq(GiftsGroupEntity::getId,param.getId()));
        if(Objects.isNull(history)){
            log.info("not found gifts group no need update...");
            return;
        }

        Date currentDate = new Date();
        UserExtensionEntity user = (UserExtensionEntity) ShiroUtils.getSubject().getPrincipal();
        GiftsGroupEntity giftsGroup = new GiftsGroupEntity();
        BeanUtils.copyProperties(param,giftsGroup);
        giftsGroup.setId(String.valueOf(param.getId()));
        giftsGroup.setMarkDeleted(StringUtils.isEmpty(param.getMarkDeleted()) ? history.getMarkDeleted() : param.getMarkDeleted());
        giftsGroup.setCreatedBy(history.getCreatedBy());
        giftsGroup.setCreatedDate(history.getCreatedDate());
        giftsGroup.setLastModifiedBy(user.getSfUserId());
        giftsGroup.setLastModifiedDate(currentDate);
        this.baseMapper.updateById(giftsGroup);
        log.info("update gifts user to group...");
        saveOrUpdateUserToGroup(giftsGroup,param);
        loadResourceService.refreshGiftGroup(giftsGroup.getId());
    }


    @Override
    @MasterTransactional
    public void saveOrUpdateUserToGroup(GiftsGroupEntity giftsGroup, GiftsGroupParam param) {
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
        log.info("delete user to group by groupId : {}", giftsGroup.getId());
        this.baseMapper.deleteUserToGroupByGroupId(giftsGroup.getId());
        List<GiftsUserToGroupEntity> userToGroupList = users.stream().map( user -> {
            GiftsUserToGroupEntity userToGroup = new GiftsUserToGroupEntity();
            BeanUtils.copyProperties(giftsGroup,userToGroup);
            userToGroup.setGroupId(giftsGroup.getId());
            userToGroup.setUserId(user.getSfUserId());
            userToGroup.setRemark(giftsGroup.getGroupCode());
            return userToGroup;
        }).collect(Collectors.toList());
        this.baseMapper.batchInsertUserToGroup(userToGroupList);
    }


    @Override
    public GiftsGroupEntity getGiftsGroupById(String id) {
        log.info("get gifts group by id: {}", id);
        GiftsGroupEntity group = this.baseMapper.selectOne(Wrappers.<GiftsGroupEntity>lambdaQuery().eq(GiftsGroupEntity::getId,id));
        List<GiftsUserToGroupEntity> userToGroupList = this.baseMapper.queryUserToGroupList(Collections.singletonList(id));
        log.info("userToGroupList size: {}", userToGroupList.size());
        group.setUserToGroups(userToGroupList);
        return group;
    }

    @Override
    public GiftsGroupEntity getGiftsGroupByCode(String code) {
        log.info("get gifts group by code: {}", code);
        GiftsGroupEntity group = this.baseMapper.selectOne(Wrappers.<GiftsGroupEntity>lambdaQuery().eq(GiftsGroupEntity::getGroupCode,code));
        if(Objects.nonNull(group)){
            List<GiftsUserToGroupEntity> userToGroupList = this.baseMapper.queryUserToGroupList(Collections.singletonList(group.getId()));
            log.info("userToGroupList size: {}", userToGroupList.size());
            group.setUserToGroups(userToGroupList);
        }
        return group;
    }

    @Override
    public List<GiftsGroupEntity> getGroupListByUserId(Long userId) {
        return this.baseMapper.queryGroupListByUserId(userId);
    }

    @Override
    public List<GiftsGroupEntity> getAllGroupList() {
        List<GiftsGroupEntity> groups = this.baseMapper.selectList(Wrappers.<GiftsGroupEntity>lambdaQuery()
                .eq(GiftsGroupEntity::getMarkDeleted,Constant.NO_EXIST_MARK));
        if(CollectionUtils.isNotEmpty(groups)){
            List<String> groupIds = groups.stream().map(GiftsGroupEntity::getId).collect(Collectors.toList());
            log.info("group ids: {}", groupIds);
            List<GiftsUserToGroupEntity> userToGroups = this.baseMapper.queryUserToGroupList(groupIds);
            Map<String, List<GiftsUserToGroupEntity>> userToGroupMap =
                    userToGroups.stream().collect(Collectors.groupingBy(GiftsUserToGroupEntity::getGroupId));
            for(GiftsGroupEntity group : groups){
                List<GiftsUserToGroupEntity> itemUserToGroups = userToGroupMap.getOrDefault(group.getId(), Collections.emptyList());
                group.setUserToGroups(itemUserToGroups);
            }
        }
        return groups;
    }


    @Override
    @MasterTransactional
    public void deleteGiftsGroup(String id) {
        log.info("logic delete gifts group...");
        this.baseMapper.update(null,Wrappers.<GiftsGroupEntity>lambdaUpdate()
                .set(GiftsGroupEntity::getMarkDeleted,Constant.EXIST_MARK)
                .eq(GiftsGroupEntity::getId,id));
        log.info("logic delete user to group...");
        this.baseMapper.logicToDeleteUserToGroupByGroupId(id);
        loadResourceService.refreshGiftGroup(id);
    }

    @Override
    public Pagination<GiftsGroupEntity> getGiftsGroupList(GiftsGroupParam param) {
        log.info("get gifts group page...");
        IPage<GiftsGroupEntity> page = this.baseMapper.queryGiftsGroupList(
                new Page<>(param.getCurrentPage(), param.getPageSize()), param);
        return new Pagination<>(page);
    }


}
