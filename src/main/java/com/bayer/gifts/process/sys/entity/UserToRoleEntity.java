package com.bayer.gifts.process.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName("t_sys_user_to_role")
@Data
public class UserToRoleEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 8720656647834663630L;
    private Long id;
    private Long roleId;
    private Long userId;
    private String createdBy;
    private String lastModifiedBy;
    private String remark;

    @TableField(exist = false)
    private String userEmail;
    @TableField(exist = false)
    private String userFirstName;
    @TableField(exist = false)
    private String userLastName;
    @TableField(exist = false)
    private String userCwid;
}
