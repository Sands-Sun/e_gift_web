package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("B_PROC_HOSPITALITY_REF")
public class HospitalityRefEntity extends GiftsBaseEntity implements Serializable {

    private static final long serialVersionUID = 7028308515207248400L;

    @TableId(type = IdType.AUTO)
    private Long refId;
    private Long applicationId;
    private String hospitalityType;
    private Double expensePerHead;
    private Integer headCount;
    private Date hospitalityDate;
    private String hospPlace;
}
