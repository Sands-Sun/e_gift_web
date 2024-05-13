package com.bayer.gifts.process.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.bayer.gifts.activiti.factory.GiftsGroupEntityManager;
import com.bayer.gifts.activiti.factory.GiftsGroupManagerFactory;
import com.bayer.gifts.activiti.factory.GiftsUserEntityManager;
import com.bayer.gifts.activiti.factory.GiftsUserManagerFactory;
import com.bayer.gifts.activiti.listener.GiftsEventListener;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class ActivitiConfig extends AbstractProcessEngineAutoConfiguration {


    @Autowired
    PlatformTransactionManager platformTransactionManager;

    @Autowired
    GiftsGroupManagerFactory giftsGroupManagerFactory;

    @Autowired
    GiftsUserManagerFactory giftsUserManagerFactory;

    @Autowired
    GiftsGroupEntityManager giftsGroupEntityManager;

    @Autowired
    GiftsUserEntityManager giftsUserEntityManager;

    @Autowired
    GiftsEventListener giftsEventListener;



    @Bean(name = "followDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.follow")
    public DataSource followDataSource(DruidProperties druidProperties) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }



    //通过@Bean注解将SpringProcessEngineConfiguration实例声明为Spring Bean，使其可供其他组件注入和使用
    @Bean
    @DependsOn("mailConfig")
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(
            @Qualifier("followDataSource") DataSource dataSource) {
        SpringProcessEngineConfiguration spec = new SpringProcessEngineConfiguration();
        //设置数据源，将注入的数据源设置到SpringProcessEngineConfiguration实例中
        spec.setDataSource(dataSource);
        //设置事务管理器将注入的事务管理器设置到SpringProcessEngineConfiguration实例中
        spec.setTransactionManager(this.platformTransactionManager);
        //设置数据库模式更新策略 true表示在启动时自动创建或更新Activiti引擎所需的数据库表结构
        spec.setDatabaseSchemaUpdate("true");
        Resource[] resources = null;
        //配置流程部署资源
        //使用PathMatchingResourcePatternResolver从classpath中的bpmn目录下加载所有以.bpmn为扩展名的文件作为流程定义资源，
        // 并将它们设置到SpringProcessEngineConfiguration实例中。
        try {
            resources = (new PathMatchingResourcePatternResolver())
                    .getResources("classpath*:porcesses/*.bpmn");
        } catch (IOException var4) {
            var4.printStackTrace();
        }
        spec.setDeploymentResources(resources);


        spec.setMailServerDefaultFrom(MailConfig.MAIL_SEND_USER);
        spec.setMailServerPort(MailConfig.MAIL_PORT);
        spec.setMailServerUsername(MailConfig.MAIL_SEND_USER);
        spec.setMailServerPassword(MailConfig.MAIL_SEND_PWD);
        spec.setMailServerHost(MailConfig.MAIL_SMTP_HOST);
//        spec.setMailServerUseSSL(true);
        

        spec.setDbIdentityUsed(false);
        List<SessionFactory> customSessionFactories = new ArrayList<>();
        // 配置自定义的用户和组管理
        spec.setUserEntityManager(giftsUserEntityManager);
        spec.setGroupEntityManager(giftsGroupEntityManager);
        //设置自定义用户和组管理的工厂
        customSessionFactories.add(giftsUserManagerFactory);
        customSessionFactories.add(giftsGroupManagerFactory);


        if (spec.getCustomSessionFactories() == null){
            spec.setCustomSessionFactories(customSessionFactories);
        }
        else{
            spec.getCustomSessionFactories().addAll(customSessionFactories);
        }

        List<ActivitiEventListener> eventListeners=new ArrayList<>();
        eventListeners.add(giftsEventListener);
        spec.setEventListeners(eventListeners);

        return spec;
    }
}
