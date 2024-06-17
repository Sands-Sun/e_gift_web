package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("B_PROC_HOSPITALITY_RECEIVING_ACTIVITY")
public class ReceivingHospActivityEntity extends GiftsActivityProcessEntity implements Serializable {
    private static final long serialVersionUID = 7169320830476768038L;
}
