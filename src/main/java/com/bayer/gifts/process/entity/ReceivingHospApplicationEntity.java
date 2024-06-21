package com.bayer.gifts.process.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bayer.gifts.process.common.Constant;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@TableName("B_PROC_HOSPITALITY_RECEIVING_APPLICATION")
public class ReceivingHospApplicationEntity extends GiftsApplicationBaseEntity implements Serializable {
    private static final long serialVersionUID = 1110928086921943209L;

    private String isInvolved;
    private String isExcluded;
    private Double estimatedTotalExpense;

    @TableField(exist = false)
    @JSONField(format = "yyyy-MM-dd")
    private Date hospitalityDate;


    @TableField(exist = false)
    private String requestType = Constant.RECEIVING_HOSPITALITY_REQUEST_TYPE;

    @TableField(exist = false)
    private ReceivingHospRefEntity hospRef;

    @TableField(exist = false)
    private List<GiftsCopyToEntity> copyToUsers;

    @TableField(exist = false)
    private List<ReceivingHospActivityEntity> hospActivities;

    @TableField(exist = false)
    private List<GiftsCompanyEntity> companyList;
}
