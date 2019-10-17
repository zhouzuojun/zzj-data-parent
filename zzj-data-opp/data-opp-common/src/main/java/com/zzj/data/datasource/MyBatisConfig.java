package com.zzj.data.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInterceptor;
import com.zzj.data.error.DataException;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import java.util.Properties;

@Configuration
public class MyBatisConfig {
    @Value("${mybatis.mapperLocations}")
    private String mapperLocations;

    @Value("${mybatis.typeAliasesPackage}")
    private String aliasesPackage;

    @Resource
    private DruidDataSource dataSource;

    @Bean
    public SqlInterceptor sqlInterceptor() {
        return new SqlInterceptor();
    }

    @Bean("sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setTypeAliasesPackage(aliasesPackage);
        //添加分页插件
        PageInterceptor iterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("returnPageInfo", "check");
        properties.setProperty("params", "count=countSql");
        iterceptor.setProperties(properties);
        bean.setPlugins(new Interceptor[]{iterceptor, sqlInterceptor()});
        ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        try {
            bean.setMapperLocations(patternResolver.getResources(mapperLocations));
            return bean.getObject();
        } catch (Exception e) {
            throw new DataException("sqlSessionFactory init is failed", e);
        }

    }


    @Bean("sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory factory) {
        return new SqlSessionTemplate(factory);
    }

    @Bean("transactionManager")
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }


}
