package com.bayer.gifts.process.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.entity.GiftsRoleEntity;
import com.bayer.gifts.process.param.GiftsRoleParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GiftsRoleDao extends BaseMapper<GiftsRoleEntity> {

    List<GiftsRoleEntity> queryRoleListByUserId(Long userId);

    IPage<GiftsRoleEntity> queryGiftsRoleList(Page<GiftsRoleEntity> page,
                                                @Param("param") GiftsRoleParam param);
}
