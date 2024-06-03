package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bayer.gifts.process.sys.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;
@Data
public class GiftsRefBaseEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -3582017905250753686L;

    // Fields
    @TableId(type = IdType.AUTO)
    private Long refId;
    private Long applicationId;
}
