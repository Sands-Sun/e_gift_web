package com.bayer.gifts.process.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bayer.gifts.process.entity.UserExtensionEntity;
import com.bayer.gifts.process.param.UserParam;
import com.bayer.gifts.process.param.UserSearchParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserExtensionDao  extends BaseMapper<UserExtensionEntity> {

    List<UserExtensionEntity> queryFuzzyUserList(@Param("param")UserSearchParam searchParam);
    IPage<UserExtensionEntity> queryUserList(Page<UserExtensionEntity> page, @Param("param") UserParam param);

    @Select("SELECT b.FIRST_NAME ,b.LAST_NAME  FROM sys_user_token a left join B_USER_EXTENSION b  on a.USER_ID =b.SF_USER_ID where a.token=#{token} and a.EXPIRE_TIME >GETDATE()   ")
    UserExtensionEntity selectUserInfoByToken(String token);
}
