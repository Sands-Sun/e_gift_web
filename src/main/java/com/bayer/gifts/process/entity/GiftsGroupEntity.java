package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@TableName("B_MD_GIFT_GROUP")
public class GiftsGroupEntity extends GiftsBaseEntity implements Serializable {

    private static final long serialVersionUID = 8058923085772243677L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private String groupName;
    private String remark;
    private Long createdBy;
    private Long lastModifiedBy;
    private String markDeleted;
    private String groupCode;

    @TableField(exist = false)
    private List<GiftsUserToGroupEntity> userToGroups;
}
