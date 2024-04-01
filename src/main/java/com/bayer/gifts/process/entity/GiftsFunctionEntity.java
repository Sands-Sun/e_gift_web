package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("B_MD_GIFT_FUNCTION")
public class GiftsFunctionEntity extends GiftsBaseEntity implements Serializable {

    private static final long serialVersionUID = 335029259405325267L;
    @TableId(type = IdType.AUTO)
    private Long id;
}
