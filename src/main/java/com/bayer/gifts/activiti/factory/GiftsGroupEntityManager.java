package com.bayer.gifts.activiti.factory;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.service.GiftsGroupService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.persistence.AbstractManager;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.GroupEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GiftsGroupEntityManager implements GroupEntityManager, Session {


    @Autowired
    GiftsGroupService giftsGroupService;


    @Override
    public Group createNewGroup(String s) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public GroupQuery createNewGroupQuery() {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public List<Group> findGroupByQueryCriteria(GroupQueryImpl groupQuery, Page page) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public long findGroupCountByQueryCriteria(GroupQueryImpl groupQuery) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public List<Group> findGroupsByUser(String s) {
        log.info("gifts findGroupsByUser: {}", s);
        List<GiftsGroupEntity>  giftsGroups = giftsGroupService.getGroupListByUserId(Long.valueOf(s));
        return giftsGroups.stream().map(g -> (Group)g).collect(Collectors.toList());
    }

    @Override
    public List<Group> findGroupsByNativeQuery(Map<String, Object> map, int i, int i1) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public long findGroupCountByNativeQuery(Map<String, Object> map) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public boolean isNewGroup(Group group) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public GroupEntity create() {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public GroupEntity findById(String s) {
        return (GroupEntity)giftsGroupService.getOne(Wrappers.<GiftsGroupEntity>lambdaQuery()
                .eq(GiftsGroupEntity::getGroupCode,s));
    }

    @Override
    public void insert(GroupEntity entity) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public void insert(GroupEntity entity, boolean b) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public GroupEntity update(GroupEntity entity) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public GroupEntity update(GroupEntity entity, boolean b) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public void delete(String s) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public void delete(GroupEntity entity) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public void delete(GroupEntity entity, boolean b) {
        throw new RuntimeException("not implement method.");
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() {

    }
}
