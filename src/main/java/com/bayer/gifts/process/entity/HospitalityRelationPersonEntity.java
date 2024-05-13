package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("B_PROC_HOSPITALITY_PERSON")
public class HospitalityRelationPersonEntity extends GiftsBaseEntity{

    private static final long serialVersionUID = 8589780167299795094L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long applicationId;
    private Long persionId;
    private String personName;
    private String companyName;
    private double money;
    private String isGoSoc;
    private String isBayerCustomer;
    private String positionType;
    private String description;
    private String markDeleted;
    private String type;
}
