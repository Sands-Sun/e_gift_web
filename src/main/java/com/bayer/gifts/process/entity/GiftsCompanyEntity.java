package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("B_MD_GIFT_COMPANY_INFO")
public class GiftsCompanyEntity extends GiftsBaseEntity implements Serializable {


    private static final long serialVersionUID = 6834066745933852224L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String companyName;
    private String description;
    private String markDeleted;
    private Long createdBy;
    private Long lastModifiedBy;
}
