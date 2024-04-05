package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

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
    private Integer departmentHeadId;
    //部门领导姓名
    private String departmentHeadName;


    @TableField(exist = false)
    private GivingGiftsRefEntity giftsRef;

    @TableField(exist = false)
    private List<GiftsCopyToEntity> copyToUsers;

    @TableField(exist = false)
    private List<GivingGiftsPersonEntity> giftsPersons;

    @TableField(exist = false)
    private List<GivingGiftsActivityEntity> giftsActivities;

}
