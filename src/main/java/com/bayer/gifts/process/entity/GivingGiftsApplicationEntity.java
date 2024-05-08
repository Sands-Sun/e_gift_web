package com.bayer.gifts.process.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bayer.gifts.process.sys.entity.FileUploadEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@TableName("B_PROC_GIVING_GIFTS_APPLICATION")
public class GivingGiftsApplicationEntity extends GiftsBaseEntity implements Serializable {


    private static final long serialVersionUID = 8862248745124601960L;
    // Fields
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

    @TableField(exist = false)
    @JSONField(format = "yyyy-MM-dd")
    private Date givenDate;

    private Long sfUserIdCreator;
    private Long supervisorId;
    private String employeeLe;
    private String reference;
    private String costCenter;
    private String status;
    private Integer giftTypeId;
    private String giftTypeName;
    private String isInvolved;
    private String isExcluded;
    private String markDeleted;
    private String department;
    private String reason;
    private String remark;

    //sky new add for bhc gift 20100126
    private Integer doctorId;
    private String requestType;
    private Integer giftToYear;
    private Integer giftToMonth;
    private String region;
    //wuhw new add for gift change 20101010

    private Double totalValue;
    private String reasonType;

    //add by wcc
    private String isUsed;

    //lining at 20150417
    private String concurStatus;
    private String concurReportId;

    //部门领导ID
    private Long departmentHeadId;
    //部门领导姓名
    private String departmentHeadName;

    private String newVersion;


    @TableField(exist = false)
    private FileUploadEntity fileAttach;

    @TableField(exist = false)
    private GivingGiftsRefEntity giftsRef;

    @TableField(exist = false)
    private List<GiftsCopyToEntity> copyToUsers;

    @TableField(exist = false)
    private List<GivingGiftsActivityEntity> giftsActivities;

    @TableField(exist = false)
    private List<GiftsCompanyEntity> companyList;

}
