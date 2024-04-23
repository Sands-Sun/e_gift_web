package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("B_PROC_RECEIVING_GIFTS_ACTIVITY")
public class ReceivingGiftsActivityEntity extends GiftsActivityBaseEntity implements Serializable {

    private static final long serialVersionUID = 1575679249960224065L;
}
