package com.atguigu.service_orders;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.atguigu"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.atguigu"})
@MapperScan(basePackages = "com/atguigu/service_orders/mapper")
public class ServiceOrdersApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceOrdersApplication.class, args);
	}

}
