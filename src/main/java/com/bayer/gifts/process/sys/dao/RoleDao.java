package com.bayer.gifts.process.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.sys.entity.RoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoleDao extends BaseMapper<RoleEntity> {
    IPage<RoleEntity> selectAll(Page<RoleEntity> page, @Param("roleName") String roleName, @Param("status") String status);
}
