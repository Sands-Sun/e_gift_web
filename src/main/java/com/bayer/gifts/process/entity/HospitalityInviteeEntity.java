package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bayer.gifts.process.sys.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("B_PROC_HOSPITALITY_INVITEE")
public class HospitalityInviteeEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 3064859593483312725L;

    @TableId(type = IdType.AUTO)
    private Long Id;
    private Long applicationId;
    private String inviteeName;
    private String companyEntity;
    private String isGoSoc;
    private String isBayerCustomer;
    private String markDeleted;
    private String positionType;
}
