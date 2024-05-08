package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("B_PROC_HOSPITALITY_APPLICATION")
public class HospitalityApplicationEntity extends GiftsBaseEntity implements Serializable {
    private static final long serialVersionUID = 5320379017314276797L;
    @TableId(type = IdType.AUTO)
    private Long applicationId;
    private Long sfProcessInsId;
    private Long sfUserIdAppliedFor;
    @TableField(exist = false)
    private String sfUserAppliedName;
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
    private String isInvolved;
    private String isExcluded;
    private String markDeleted;
    private String department;
    private String reason;
    private String remark;
    private Double estimatedTotalExpense;
    private String attachmentFile;
    private String attachmentFileName;
    private String isUsed;
    private Long departmentHeadId;
    private String departmentHeadName;
    private String bhcParticipantsFile;
    private String bhcParticipantsFilename;
    private String newVersion;
}
