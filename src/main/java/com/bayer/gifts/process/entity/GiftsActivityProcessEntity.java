package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class GiftsActivityProcessEntity extends GiftsActivityBaseEntity{

    private Long sfProcessInsId;
    private Long sfActivityInsId;

}
