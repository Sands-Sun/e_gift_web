package com.bayer.gifts.process.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

@TableName("t_sys_role")
public class RoleEntity {

    private Integer id;

    private String roleName;

    private String remark;

    private Date createDate;

    private Integer createBy;

    private Date updateDate;

    private Integer markForDelete;

    private String functions;
}
