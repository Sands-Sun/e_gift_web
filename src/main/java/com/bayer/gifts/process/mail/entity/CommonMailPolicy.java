package com.bayer.gifts.process.mail.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("B_MD_GIFT_CLAIM_MAIL_POLICY")
public class CommonMailPolicy implements Serializable{

	private static final long serialVersionUID = -5756640434415785101L;
	@TableId
	private Long id;
	private String processType;
	private String mailType;
	private String subject;
	private String template;
	private String mailTo;
	private String markDeleted;
	private Date createdDate;
	private Date lastModifiedDate;
}
