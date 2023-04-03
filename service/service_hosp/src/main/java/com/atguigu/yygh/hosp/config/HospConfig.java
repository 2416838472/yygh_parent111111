package com.atguigu.yygh.hosp.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration //配置类
@EnableTransactionManagement //开启事务
@MapperScan("com.atguigu.yygh.hosp.mapper")
public class HospConfig {

}
