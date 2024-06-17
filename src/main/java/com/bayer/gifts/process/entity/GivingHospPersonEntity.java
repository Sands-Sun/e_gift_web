package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bayer.gifts.process.sys.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("B_PROC_HOSPITALITY_PERSION")
public class GivingHospPersonEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 2146193450174266247L;

    @TableId(type = IdType.AUTO)
    private Long Id;
    private Long applicationId;
    private Long inviteeId;
    private Long persionId;
    private String personName;
    private String companyName;
    private Double money;
    private String description;
    private String markDeleted;
    private String positionType;
    private String positionTitle;
}
