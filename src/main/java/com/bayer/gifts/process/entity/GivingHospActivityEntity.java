package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("B_PROC_HOSPITALITY_ACTIVITY")
public class GivingHospActivityEntity extends GiftsActivityProcessEntity implements Serializable {
    private static final long serialVersionUID = -8557954725580561070L;
}
