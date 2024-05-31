package com.bayer.gifts.process.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Configuration
public class JsonConfig extends WebMvcConfigurationSupport {
    /**
     * 使用fastjson代替jackson
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

        //自定义fastjson配置
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.QuoteFieldNames, // 双引号
                SerializerFeature.WriteMapNullValue, // 输入空值字段
                SerializerFeature.WriteEnumUsingToString, // 枚举输出STRING
                SerializerFeature.WriteNullBooleanAsFalse, // 布尔类型如果为null输出false
                SerializerFeature.WriteNullListAsEmpty, // List字段如果为null输出为[]
                //SerializerFeature.WriteNullNumberAsZero, // number类型如果为null输出0
                //SerializerFeature.WriteNullStringAsEmpty, // 字符串类型如果为null输出""
                SerializerFeature.SortField, // 按字段名称排序后进行输出
                SerializerFeature.WriteDateUseDateFormat // 设置日期格式
        );

        //在convert中添加配置信息
        fastConverter.setFastJsonConfig(fastJsonConfig);
        //设置支持的媒体类型
        fastConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        //设置默认字符集
        fastConverter.setDefaultCharset(StandardCharsets.UTF_8);
        //将convert添加到converters
        converters.add(0, fastConverter);

        //解决返回字符串带双引号问题
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_PLAIN));
        stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
        converters.add(0, stringHttpMessageConverter);

        super.addDefaultHttpMessageConverters(converters);

    }


    /**
     * 添加静态资源
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/templates/")
                .addResourceLocations("classpath:/META-INF/resources/");
    }
    /**
     * 跨域支持
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //对哪些目录可以跨域访问
        registry.addMapping("/**")
                //允许哪些网站可以跨域访问
                .allowedOrigins("*")
                //允许哪些方法
                .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH", "OPTIONS", "HEAD")
                .maxAge(3600 * 24);
    }



    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> messageConverter : converters) {
            System.out.println("=====================" + messageConverter);
        }
    }
    
}
