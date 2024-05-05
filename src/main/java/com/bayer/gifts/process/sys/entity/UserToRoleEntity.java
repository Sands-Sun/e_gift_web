package com.bayer.gifts.process.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

@TableName("t_sys_user_to_role")
public class UserToRoleEntity {

    private Integer id;

    private Integer roleId;

    private Integer userId;

    private Date createDate;
}
