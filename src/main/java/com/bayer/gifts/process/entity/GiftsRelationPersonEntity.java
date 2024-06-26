package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bayer.gifts.process.sys.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("B_PROC_GIVING_GIFTS_PERSION")
public class GiftsRelationPersonEntity extends GiftsRelationPersonBaseEntity implements Serializable {

    private static final long serialVersionUID = 8589780167299795094L;
//    @TableId(type = IdType.AUTO)
//    private Long id;
//    private Long applicationId;
//    private Long persionId;
//    private String personName;
//    private String companyName;
    @TableField(exist = false)
    private String positionTitle;
    private Integer volume;
//    private Double money;
//    private String description;
//    private String markDeleted;
//    private String type;
}
