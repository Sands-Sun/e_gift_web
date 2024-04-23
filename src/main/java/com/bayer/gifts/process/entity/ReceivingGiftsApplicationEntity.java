package com.bayer.gifts.process.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bayer.gifts.process.common.Constant;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@TableName("B_PROC_RECEIVING_GIFTS_APPLICATION")
public class ReceivingGiftsApplicationEntity extends GiftsBaseEntity implements Serializable {

    private static final long serialVersionUID = -2033392518976897300L;
    @TableId(type = IdType.AUTO)
    private Long applicationId;
    private Long sfUserIdAppliedFor;
    @TableField(exist = false)
    private String sfUserAppliedName;
    @TableField(exist = false)
    private String sfUserAppliedCwid;
    @TableField(exist = false)
    private String sfUserAppliedEmail;

    @TableField(exist = false)
    @JSONField(format = "yyyy-MM-dd")
    private Date givingDate;
    private Long sfUserIdCreator;
    private Long supervisorId;
    private String employeeLe;
    private String reference;
    private String costCenter;
    private Double estimatedTotalValue;
    // 状态
    private String status;
    // 礼品已上交SCO
    private String isHandedOver;
    // 是否属于列出的情况
    private String isInvolved;
    // 是否不包括列出的情况
    private String isExcluded;
    // 删除标记
    private String markDeleted;
    // 申请者的部门
    private String department;
    // 接受礼品的原因
    private String reason;

    private String reasonType;
    // 备注
    private String remark;

    @TableField(exist = false)
    private String requestType = Constant.RECEIVING_GIFTS_REQUEST_TYPE;

    @TableField(exist = false)
    private ReceivingGiftsRefEntity giftsRef;

    @TableField(exist = false)
    private List<GiftsCopyToEntity> copyToUsers;

    @TableField(exist = false)
    private List<ReceivingGiftsActivityEntity> giftsActivities;
}
