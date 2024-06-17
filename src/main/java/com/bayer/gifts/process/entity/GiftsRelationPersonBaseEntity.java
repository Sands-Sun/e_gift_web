package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bayer.gifts.process.sys.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class GiftsRelationPersonBaseEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -1233118260048060102L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long applicationId;
    private Long persionId;
    private String personName;
    private String companyName;
    private Double money;
    private String description;
    private String markDeleted;
    private String type;
}
