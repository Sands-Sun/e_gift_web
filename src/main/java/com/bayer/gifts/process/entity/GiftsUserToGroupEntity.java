package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bayer.gifts.process.sys.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class GiftsUserToGroupEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -8931236536999356144L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private String groupId;
    private Long userId;
    private String remark;
    private Long createdBy;
    private Long lastModifiedBy;
    private String markDeleted;

    @TableField(exist = false)
    private String userEmail;
    @TableField(exist = false)
    private String userFirstName;
    @TableField(exist = false)
    private String userLastName;
    @TableField(exist = false)
    private String userCwid;
    @TableField(exist = false)
    private GiftsCountryHeadToSupervisorEntity countryHeadToSupervisor;
}
