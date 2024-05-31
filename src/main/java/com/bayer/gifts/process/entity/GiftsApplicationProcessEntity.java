package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class GiftsApplicationProcessEntity extends GiftsApplicationBaseEntity{
    private static final long serialVersionUID = -1601168228006288534L;
    private Long sfProcessInsId;
    //部门领导ID
    private Long departmentHeadId;
    //部门领导姓名
    private String departmentHeadName;

    @TableField(exist = false)
    private GiftsGroupEntity deptHeadGroup;
}
