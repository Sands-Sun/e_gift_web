package com.bayer.gifts;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

//@EnableTransactionManagement

@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication(exclude =
        {       DataSourceAutoConfiguration.class,
                MybatisPlusAutoConfiguration.class,
                org.activiti.spring.boot.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
//@ComponentScan(basePackages = { "com.bayer.gifts" })
public class SpringBootAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAdminApplication.class, args);
    }

}
