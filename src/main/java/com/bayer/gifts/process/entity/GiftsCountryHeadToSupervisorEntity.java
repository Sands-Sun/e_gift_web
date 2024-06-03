package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bayer.gifts.process.sys.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("B_MD_GIFT_COUNTRY_HEAD_TO_SUPERVISOR")
public class GiftsCountryHeadToSupervisorEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 3959761133703558508L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long supervisorId;
    private String remark;
    private Long createdBy;
    private Long lastModifiedBy;
    private String markDeleted;
}
