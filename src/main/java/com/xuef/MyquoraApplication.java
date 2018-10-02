package com.xuef;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xuef.dao") // 将项目中对应的mapper类的路径加进来
public class MyquoraApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyquoraApplication.class, args);
	}
}
