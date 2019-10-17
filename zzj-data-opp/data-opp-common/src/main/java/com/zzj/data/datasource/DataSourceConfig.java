package com.zzj.data.datasource;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.zzj.data.error.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataSourceConfig {
    private final Logger logger= LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${db.url}")
    private String url;

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;

    @Value("${db.driver}")
    private String driver;

    @Value("${db.maxActive}")
    private Integer maxActive;

    @Value("${db.minIdle}")
    private Integer minIdle;

    @Value("${db.initSize}")
    private Integer initSize;

    @Value("${db.testSql}")
    private String testSql;

    @Bean(name = "dataSource", destroyMethod = "close")
    public DruidDataSource druidDataSource() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(this.url);
        dataSource.setDriverClassName(this.driver);
        dataSource.setUsername(this.username);
        dataSource.setPassword(this.password);
        dataSource.setMaxWait(60000);
        dataSource.setTimeBetweenEvictionRunsMillis(300000);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setTestWhileIdle(true);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setMaxActive(maxActive);
        dataSource.setInitialSize(initSize);
        dataSource.setMinIdle(minIdle);
        logger.error("启动数据服务");
        List<Filter> filters= new ArrayList<Filter>();
        dataSource.setProxyFilters(filters);
        try {
            dataSource.init();
        }catch(SQLException e) {
            logger.error("初始化数据连接池失败:{}", e);
            throw new DataException("init druid pool is failed",e);

        }

        return dataSource;

    }



    @Bean
    public WallFilter wallFiler() {
        WallFilter filter=new WallFilter();
        filter.setConfig(this.wallConfig());
        return filter;
    }

    @Bean
    public WallConfig wallConfig() {
        WallConfig config= new WallConfig();
        config.setMultiStatementAllow(true);
        config.setNoneBaseStatementAllow(true);
        return config;
    }

}
