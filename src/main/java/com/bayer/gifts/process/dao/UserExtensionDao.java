package com.bayer.gifts.process.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.param.UserSearchParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserExtensionDao  extends BaseMapper<UserExtensionEntity> {

    List<UserExtensionEntity> queryFuzzyUserList(@Param("param")UserSearchParam searchParam);
}
