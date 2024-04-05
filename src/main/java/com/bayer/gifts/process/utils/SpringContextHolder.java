/*
 * @(#)SpringContextHolder.java	2016年8月31日上午10:49:42
 * Copyright 2016 DNE All rights reserved.
 */
package com.bayer.gifts.process.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 
 * 类<strong>SpringContextHolder.java</strong>{Spring Bean 工具类}
 */
@Component
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		SpringContextHolder.context = context;
	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) throws BeansException {
		return (T)context.getBean(name);
	}

	public static <T> T getBean(Class<T> tClass) throws BeansException{
		return (T)context.getBean(tClass);
	}


	public static <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		return (T)context.getBean(name, requiredType);
	}

	public static boolean containsBean(String name) {
		return context.containsBean(name);
	}

	public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		return context.isSingleton(name);
	}

	public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		return context.getType(name);
	}

	public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
		return context.getAliases(name);
	}

	@Override
	public void destroy() throws Exception {
		context = null;
	}

}
