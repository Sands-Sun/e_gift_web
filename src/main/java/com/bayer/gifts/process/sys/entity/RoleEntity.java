package com.bayer.gifts.process.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@TableName("t_sys_role")
@Data
public class RoleEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -904106579212707324L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private String roleName;
    private String remark;
    private String createdBy;
    private String lastModifiedBy;
    private String markDeleted;
    private String functions;

    @TableField(exist = false)
    private List<UserToRoleEntity> userToRoles;
}
