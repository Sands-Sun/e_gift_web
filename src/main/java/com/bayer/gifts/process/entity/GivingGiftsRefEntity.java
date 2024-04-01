package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("B_PROC_GIVING_GIFTS_REF")
public class GivingGiftsRefEntity extends GiftsBaseEntity  implements Serializable {

    private static final long serialVersionUID = 404893311870640317L;
    // Fields
    @TableId(type = IdType.AUTO)
    private Long refId;
    private Long applicationId;
    private String giftDesc;
    private Double unitValue;
    private Integer volume;
    private Date givenDate;

    private String givenPerson;
    private String givenCompany;
    //sky new add for bhc gift 20100126
    private Long categoryId;

    @TableField(exist = false)
    private String categoryName;

    @TableField(exist = false)
    private String datestr;
    //wuhw new add for gift change 20101010
    private String giftDescType;
    private String attachmentFile;
    private String attachmentFileName;
    private String isGoSoc;
    private String isBayerCustomer;
}
