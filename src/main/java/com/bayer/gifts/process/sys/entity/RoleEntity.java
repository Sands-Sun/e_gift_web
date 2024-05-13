package com.bayer.gifts.process.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("t_sys_role")
@Data
public class RoleEntity {

    private Integer id;

    private String roleName;

    private String remark;

    private Date createDate;

    private Long createBy;

    private Date updateDate;

    private Long updateBy;

    private String markForDelete;

    private String functions;
}
