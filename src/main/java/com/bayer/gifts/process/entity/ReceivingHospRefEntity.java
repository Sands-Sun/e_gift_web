package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("B_PROC_HOSPITALITY_RECEIVING_REF")
public class ReceivingHospRefEntity extends GiftsRefBaseEntity implements Serializable {
    private static final long serialVersionUID = 4892962150442578544L;

    private String hospitalityType;
    private Double expensePerHead;
    private Integer headCount;
    private Date hospitalityDate;
    private String hospPlace;
    private String companyName;
    private String personName;
    private String positionTitle;
}
