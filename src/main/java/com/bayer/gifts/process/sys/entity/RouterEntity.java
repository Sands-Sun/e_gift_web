package com.bayer.gifts.process.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 路由接收对象
 * @create 2024-04-25
 * @author lvlei
 */
@Data
@TableName("t_sys_router")
public class RouterEntity {

    private Integer id;

    @TableField(value = "pId")
    private Integer pId;

    private String name;

    private String path;

    private String component;

    @TableField(exist = false)
    private List<RouterEntity> children=new ArrayList<>();

    @TableField(exist = false)
    private RouterMetaEntity meta;

}
