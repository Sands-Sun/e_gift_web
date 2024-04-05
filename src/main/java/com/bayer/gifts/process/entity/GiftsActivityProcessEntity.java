package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class GiftsActivityProcessEntity extends GiftsBaseEntity{

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
