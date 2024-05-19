package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("B_MD_GIFT_DICTIONARY")
public class GiftsDictionaryEntity extends GiftsBaseEntity implements Serializable {

    private static final long serialVersionUID = -8987750150662091935L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private String category;
    private String company;
    private String type;
    private String typeDesc;
    private String language;
    private String code;
    private String name;
    private int dIndex;
    private String remark;
    private Long createdBy;
    private Long lastModifiedBy;
    private String markDeleted;
}
