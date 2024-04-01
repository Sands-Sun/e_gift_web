package com.bayer.gifts.activiti.management;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bayer.gifts.process.dao.UserExtensionDao;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.Picture;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.AbstractManager;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.persistence.entity.UserEntityManager;

import java.util.List;
import java.util.Map;

@Slf4j
public class CustomUserEntityManager extends AbstractManager implements UserEntityManager {

    private UserExtensionDao userExtensionDao;

    public CustomUserEntityManager(ProcessEngineConfigurationImpl processEngineConfiguration) {
        super(processEngineConfiguration);
    }


    public CustomUserEntityManager(ProcessEngineConfigurationImpl processEngineConfiguration,
                                   UserExtensionDao userExtensionDao) {
        super(processEngineConfiguration);
        this.userExtensionDao = userExtensionDao;
    }


    @Override
    public User createNewUser(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateUser(User user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<User> findUserByQueryCriteria(UserQueryImpl userQuery, Page page) {
        return null;
    }

    @Override
    public long findUserCountByQueryCriteria(UserQueryImpl userQuery) {
        return 0;
    }

    @Override
    public List<Group> findGroupsByUser(String s) {
        return null;
    }

    @Override
    public UserQuery createNewUserQuery() {
        return null;
    }

    @Override
    public Boolean checkPassword(String s, String s1) {
        return null;
    }

    @Override
    public List<User> findUsersByNativeQuery(Map<String, Object> map, int i, int i1) {
        return null;
    }

    @Override
    public long findUserCountByNativeQuery(Map<String, Object> map) {
        return 0;
    }

    @Override
    public boolean isNewUser(User user) {
        return false;
    }

    @Override
    public Picture getUserPicture(String s) {
        return null;
    }

    @Override
    public void setUserPicture(String s, Picture picture) {

    }

    @Override
    public void deletePicture(User user) {

    }

    @Override
    public UserEntity create() {
        return null;
    }

    @Override
    public UserEntity findById(String s) {
        UserExtensionEntity entity = userExtensionDao.selectOne(
                Wrappers.<UserExtensionEntity>lambdaQuery().eq(UserExtensionEntity::getCwid, s));
        log.info("user information: {}", entity);
        return entity;
    }

    @Override
    public void insert(UserEntity entity) {

    }

    @Override
    public void insert(UserEntity entity, boolean b) {

    }

    @Override
    public UserEntity update(UserEntity entity) {
        return null;
    }

    @Override
    public UserEntity update(UserEntity entity, boolean b) {
        return null;
    }

    @Override
    public void delete(String s) {

    }

    @Override
    public void delete(UserEntity entity) {

    }

    @Override
    public void delete(UserEntity entity, boolean b) {

    }
}
