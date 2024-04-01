package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("B_PROC_GIVING_GIFTS_ACTIVITY")
public class GivingGiftsActivityEntity extends GiftsBaseEntity implements Serializable {

    private static final long serialVersionUID = -9007695963709382268L;
    // Fields
    @TableId(type = IdType.AUTO)
    private Long appActivityDataId;
    private Long applicationId;
    private Long sfProcessInsId;
    private Long sfActivityInsId;
    private Long sfUserIdSubmitter;
    private String action;
    private String remark;
    @TableField(exist = false)
    private String userFirstName;
    @TableField(exist = false)
    private String userLastName;
    @TableField(exist = false)
    private String userEmail;
}
