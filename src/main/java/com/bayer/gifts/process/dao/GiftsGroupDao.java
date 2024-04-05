package com.bayer.gifts.process.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.common.validator.group.Group;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.entity.GiftsUserToGroupEntity;
import com.bayer.gifts.process.param.GiftsGroupParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface GiftsGroupDao extends BaseMapper<GiftsGroupEntity> {

    void deleteUserToGroupByGroupId(String groupId);

    void logicToDeleteUserToGroupByGroupId(String groupId);

    List<GiftsUserToGroupEntity> queryUserToGroupList(Collection<String> groupIds);
    void batchInsertUserToGroup(@Param("items")Collection<GiftsUserToGroupEntity> list);
    List<GiftsGroupEntity> queryGroupListByUserId(Long userId);

    IPage<GiftsGroupEntity> queryGiftsGroupList(Page<GiftsGroupEntity> page,
                                                @Param("param") GiftsGroupParam param);
}
