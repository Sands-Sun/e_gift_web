package com.bayer.gifts.process.sys.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_sys_router_meta")
public class RouterMetaEntity {

    private Integer id;

    private String title;
    @TableField(value = "i18nKey")
    private String i18nKey;

    private String icon;
    @TableField(value = "orderId")
    private Integer orderId;
    @TableField(value = "hideInMenu")
    private Integer hideInMenu;
    @TableField(value = "routerId")
    private Integer routerId;

    @TableField(exist = false)
    private Integer order;

}
