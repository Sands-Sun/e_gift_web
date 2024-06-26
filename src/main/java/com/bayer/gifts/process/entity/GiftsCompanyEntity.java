package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bayer.gifts.process.sys.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@TableName("B_MD_GIFT_COMPANY_INFO")
public class GiftsCompanyEntity extends BaseEntity implements Serializable {


    private static final long serialVersionUID = 6834066745933852224L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String companyName;
    private String description;
    private String markDeleted;
    private Long createdBy;
    private Long lastModifiedBy;
    @TableField(exist = false)
    private List<GiftsPersonEntity> personList;
}
