package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("B_MD_GIFT_ROLE")
public class GiftsRoleEntity extends GiftsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1329423102423136895L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private String roleName;
    private String remark;
    private Long createdBy;
    private Long lastModifiedBy;
    private String markDeleted;
    private String roleCode;
}
