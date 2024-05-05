package com.bayer.gifts.process.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bayer.gifts.process.sys.entity.UserToRoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserToRoleDao extends BaseMapper<UserToRoleEntity> {
    @Select("select B.FUNCTIONS  from t_sys_user_to_role tsutr left join t_sys_role b on tsutr.ROLE_ID=b.ID where B.MARK_FOR_DELETE =0 AND tsutr.USER_ID =#{userId}")
    String selectUserRouteIds(Long userId);

}
