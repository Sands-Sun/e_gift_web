
package com.bayer.gifts.process.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Role;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.event.TransactionalEventListenerFactory;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.sql.DataSource;

/**
 * mybatis-plus配置
 */
@Configuration
@MapperScan(basePackages ="com.bayer.gifts.process.**.dao",
        sqlSessionTemplateRef  = "masterSqlSessionTemplate",
        sqlSessionFactoryRef = "masterSqlSessionFactory")
public class MybatisPlusConfig extends AbstractMybatisPlusConfiguration{

    @Value("${mybatis-plus.master.mapper-locations}")
    private String mapperResource;

    @Value("${mybatis-plus.master.typeAliasesPackage}")
    private String typeAliasesPackage;



    @Bean(name = "masterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.master")
//    @Primary
    public DataSource masterDataSource(DruidProperties druidProperties) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return druidProperties.dataSource(dataSource);
    }


    @Bean(name = "masterMybatisPlusProperties")
    @ConfigurationProperties(prefix = "mybatis-plus")
    public MybatisPlusProperties mybatisPlusProperties() {
        MybatisPlusProperties properties = new MybatisPlusProperties();
        properties.setMapperLocations(new String[]{mapperResource});
        properties.setTypeAliasesPackage(typeAliasesPackage);
        return properties;
    }


    //主数据源 ds1数据源
//    @Primary
    @Bean("masterSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean(
            @Qualifier("masterDataSource") DataSource dataSource,
            @Qualifier("masterMybatisPlusProperties") MybatisPlusProperties properties,
            ResourceLoader resourceLoader,
            ApplicationContext applicationContext) throws Exception {
        return getSqlSessionFactory(dataSource,
                properties,
                resourceLoader,
                null,
                null,
                applicationContext);
    }

//    @Primary
    @Bean(name = "masterSqlSessionTemplate")
    public SqlSessionTemplate masterSqlSessionTemplate(
            @Qualifier("masterMybatisPlusProperties") MybatisPlusProperties properties,
            @Qualifier("masterSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return getSqlSessionTemplate(sqlSessionFactory,properties);
    }


    @Primary
    @Bean(name = "masterTransactionManager")
    public DataSourceTransactionManager masterTransactionManager(
                @Qualifier("masterDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "masterBeanFactoryTransactionAttributeSourceAdvisor")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public BeanFactoryTransactionAttributeSourceAdvisor transactionAdvisor(
            @Qualifier("masterTransactionAttributeSource") TransactionAttributeSource transactionAttributeSource,
            @Qualifier("masterTransactionInterceptor") TransactionInterceptor transactionInterceptor) {

        BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
        advisor.setTransactionAttributeSource(transactionAttributeSource);
        advisor.setAdvice(transactionInterceptor);
        advisor.setOrder(100000);
        return advisor;
    }

    @Bean(name = "masterTransactionAttributeSource")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public TransactionAttributeSource transactionAttributeSource() {
        return new AnnotationTransactionAttributeSource();
    }



    @Bean(name = "masterTransactionInterceptor")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public TransactionInterceptor transactionInterceptor(
            @Qualifier("masterTransactionAttributeSource") TransactionAttributeSource transactionAttributeSource,
            @Qualifier("masterTransactionManager") PlatformTransactionManager txManager
    ) {
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionAttributeSource(transactionAttributeSource);
        interceptor.setTransactionManager(txManager);
        return interceptor;
    }


    @Bean(name = "masterTransactionalEventListenerFactory")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static TransactionalEventListenerFactory transactionalEventListenerFactory() {
        return new TransactionalEventListenerFactory();
    }


    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor pagination = new PaginationInterceptor();
//        pagination.setDbType(DbType.SQL_SERVER);
        return pagination;
    }

}
