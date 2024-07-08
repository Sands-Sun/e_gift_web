package com.bayer.gifts.process.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bayer.gifts.process.sys.entity.FileUploadEntity;
import com.bayer.gifts.process.sys.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GiftsApplicationBaseEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 3685607462783746041L;

    @TableId(type = IdType.AUTO)
    private Long applicationId;
    private Long sfUserIdAppliedFor;
    @TableField(exist = false)
    private String sfUserAppliedName;
    @TableField(exist = false)
    private String sfUserAppliedFirstName;
    @TableField(exist = false)
    private String sfUserAppliedLastName;
    @TableField(exist = false)
    private String sfUserAppliedCwid;
    @TableField(exist = false)
    private String sfUserAppliedEmail;
    private Long sfUserIdCreator;
    private Long supervisorId;
    private String employeeLe;
    private String reference;
    private String costCenter;
    private String status;
    private String reason;
    private String remark;
    private String markDeleted;
    private String department;

    @TableField(exist = false)
    private UserExtensionEntity applyForUser;
    @TableField(exist = false)
    private UserExtensionEntity creatorUser;
    @TableField(exist = false)
    private FileUploadEntity fileAttach;
    @TableField(exist = false)
    private List<FileUploadEntity> extraAttachments;
    @TableField(exist = false)
    private List<GiftsCopyToEntity> copyToUsers;
}
