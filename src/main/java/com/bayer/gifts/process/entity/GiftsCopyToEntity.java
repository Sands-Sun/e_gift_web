package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bayer.gifts.process.sys.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("B_PROC_COMMON_MSG_COPYTO")
public class GiftsCopyToEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 4929254954063901959L;
    @TableId(type = IdType.AUTO)
    private Long messageCopyToId;
    private Long applicationId;
    private Long sfUserIdFrom;
    private Long sfUserIdCopyTo;
    private String type;

    // sky add for Global Email inform 2007-3-7
    private String copytoCwid;
    private String copytoFirstName;
    private String copytoLastName;
    @TableField(exist = false)
    private String copytoEmail;
}
