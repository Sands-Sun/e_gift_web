package com.bayer.gifts.process.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bayer.gifts.process.sys.entity.UserToRoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserToRoleDao extends BaseMapper<UserToRoleEntity> {

}
