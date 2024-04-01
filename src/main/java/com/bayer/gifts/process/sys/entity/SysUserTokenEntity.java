/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.bayer.gifts.process.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.shiro.authc.AuthenticationToken;

import java.io.Serializable;
import java.util.Date;


/**
 * 系统用户Token
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@TableName("sys_user_azure_token")
public class SysUserTokenEntity implements Serializable, AuthenticationToken {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.INPUT)
	private String state;
	//用户ID
	private Long userId;
	//token
	private String token;
	//过期时间
	private Date expireTime;

	private Date createTime;

	public SysUserTokenEntity() {

	}

	public SysUserTokenEntity(String token) {
		this.token = token;
	}

	@Override
	public Object getPrincipal() {
		return token;
	}

	@Override
	public Object getCredentials() {
		return token;
	}
}
