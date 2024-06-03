package com.bayer.gifts.process.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.sys.entity.RoleEntity;
import com.bayer.gifts.process.sys.entity.UserToRoleEntity;
import com.bayer.gifts.process.sys.param.RoleParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface RoleDao extends BaseMapper<RoleEntity> {
    IPage<RoleEntity> selectAll(Page<RoleEntity> page, @Param("param")RoleParam param);
    void deleteUserToRoleByRoleId(Long roleId);
    void batchDeleteUserToRoleByRoleIds(Collection<String> roleIds);
    void batchInsertUserToRole(@Param("items") Collection<UserToRoleEntity> list);
    List<UserToRoleEntity> queryUserToRoleList(Collection<Long> roleIds);

    List<RoleEntity> queryRoleByUserId(Long userId);
}
