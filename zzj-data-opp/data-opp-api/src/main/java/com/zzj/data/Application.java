package com.zzj.data;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 服务启动类
 * @author zhouzj 2019年9月28日
 * @Email 15112643101@163.com
 */
@SpringBootApplication
@MapperScan("com.zzj.data.*.dao")
@EnableTransactionManagement
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
