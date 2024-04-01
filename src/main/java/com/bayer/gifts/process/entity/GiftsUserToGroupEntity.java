package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class GiftsUserToGroupEntity extends GiftsBaseEntity implements Serializable {

    private static final long serialVersionUID = -8931236536999356144L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long groupId;
    private Long userId;
    private String remark;
    private Long createdBy;
    private Long lastModifiedBy;
    private String markDeleted;
}
