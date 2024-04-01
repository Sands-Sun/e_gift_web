package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("B_PROC_RECEIVING_GIFTS_ACTIVITY")
public class ReceivingGiftsActivityEntity extends GiftsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1575679249960224065L;
    @TableId(type = IdType.AUTO)
    private Long appActivityDataId;
    private Long applicationId;
    private Long sfUserIdSubmitter;
    private String action;
    private String remark;
}
