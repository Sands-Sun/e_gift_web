package com.bayer.gifts.process.mail.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@TableName("B_MD_GIFT_MAIL_TEMPLATE")
public class MailTemplate implements Serializable {

    private static final long serialVersionUID = 2331555541825765627L;

    @TableId
    private Long id;
    private String processType;
    private String mailType;
    private String template;
    private String markDeleted;
    private Date createdDate;
    private Date lastModifiedDate;

    @TableField(exist = false)
    private List<MailPolicy> policyList;
}
