package com.bayer.gifts.process.mail.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("B_MD_GIFT_MAIL_POLICY")
public class MailPolicy implements Serializable{

	private static final long serialVersionUID = -5756640434415785101L;
	@TableId
	private Long id;
	private Long templateId;
	private String actionType;
	private String subject;
	private String mailTo;
	private String markDeleted;
	private Date createdDate;
	private Date lastModifiedDate;
}
