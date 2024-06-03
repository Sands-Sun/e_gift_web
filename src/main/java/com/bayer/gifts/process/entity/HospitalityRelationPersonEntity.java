package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bayer.gifts.process.sys.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("B_PROC_HOSPITALITY_PERSON")
public class HospitalityRelationPersonEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 5609619173848697506L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long applicationId;
    private Long persionId;
    private String personName;
    private String companyName;
    private double money;
    private String isGoSoc;
    @TableField(exist = false)
    private String isGoSocNameCN;
    @TableField(exist = false)
    private String isGoSocNameEN;

    private String isBayerCustomer;
    @TableField(exist = false)
    private String isBayerCustomerCN;
    @TableField(exist = false)
    private String isBayerCustomerEN;

    private String positionTitle;
    private String description;
    private String markDeleted;
    private String type;
}
