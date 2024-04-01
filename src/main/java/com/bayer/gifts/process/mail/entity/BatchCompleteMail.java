package com.bayer.gifts.process.mail.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("B_MD_GIFT_BATCH_COMPLETE_MAIL")
public class BatchCompleteMail {

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private String mailSender;
    private String mailFrom;
    private String mailTo;
    private String mailCc;
    private String mailBcc;
    private String mailSubject;
    private String mailBody;
    private String mailAttachment;
    private Date createDate;
    private Date sentDate;
    private String isSent;
    private Integer wrongTimes;
    private String executionId;
    private String taskId;
}
