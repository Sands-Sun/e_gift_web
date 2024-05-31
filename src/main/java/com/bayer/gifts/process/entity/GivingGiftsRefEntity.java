package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@TableName("B_PROC_GIVING_GIFTS_REF")
public class GivingGiftsRefEntity extends GiftsRefBaseEntity  implements Serializable {

    private static final long serialVersionUID = 404893311870640317L;

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
    @TableField(exist = false)
    private String giftDescTypeCN;
    @TableField(exist = false)
    private String giftDescTypeEN;

    private String attachmentFile;
    private String attachmentFileName;
    private String isGoSoc;
    @TableField(exist = false)
    private String isGoSocNameCN;
    @TableField(exist = false)
    private String isGoSocNameEN;
    private String isBayerCustomer;
    @TableField(exist = false)
    private String isBayerCustomerCN;
    @TableField(exist = false)
    private String isBayerCustomerEN;

//    @TableField(exist = false)
//    private List<GiftsRelationPersonEntity> giftsPersons;
}
