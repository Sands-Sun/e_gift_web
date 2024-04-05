package com.bayer.gifts.activiti.factory;

import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.service.GiftsGroupService;
import com.bayer.gifts.process.service.UserInfoService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.Picture;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.persistence.AbstractManager;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.persistence.entity.UserEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GiftsUserEntityManager implements UserEntityManager , Session {


    @Autowired
    UserInfoService userInfoService;

    @Autowired
    GiftsGroupService giftsGroupService;


    @Override
    public User createNewUser(String s) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public void updateUser(User user) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public List<User> findUserByQueryCriteria(UserQueryImpl userQuery, Page page) {
        User user = userInfoService.getById(userQuery.getId());
        List<User> userList = new ArrayList<>();
        if (user != null) {
            userList.add(user);
        }
        return userList;
    }

    @Override
    public long findUserCountByQueryCriteria(UserQueryImpl userQuery) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public List<Group> findGroupsByUser(String s) {
        log.info("gifts findGroupsByUser: {}", s);
        List<GiftsGroupEntity>  giftsGroups = giftsGroupService.getGroupListByUserId(Long.valueOf(s));
        return giftsGroups.stream().map(g -> (Group)g).collect(Collectors.toList());
    }

    @Override
    public UserQuery createNewUserQuery() {
        return null;
    }

    @Override
    public Boolean checkPassword(String s, String s1) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public List<User> findUsersByNativeQuery(Map<String, Object> map, int i, int i1) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public long findUserCountByNativeQuery(Map<String, Object> map) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public boolean isNewUser(User user) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public Picture getUserPicture(String s) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public void setUserPicture(String s, Picture picture) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public void deletePicture(User user) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public UserEntity create() {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public UserEntity findById(String s) {
        return (UserEntity) userInfoService.getById(Long.valueOf(s));
    }

    @Override
    public void insert(UserEntity entity) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public void insert(UserEntity entity, boolean b) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public UserEntity update(UserEntity entity) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public UserEntity update(UserEntity entity, boolean b) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public void delete(String s) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public void delete(UserEntity entity) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public void delete(UserEntity entity, boolean b) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() {

    }
}
