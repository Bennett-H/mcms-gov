package net.mingsoft;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication(scanBasePackages = {"net.mingsoft"})
@MapperScan(basePackages={"**.dao","com.baomidou.**.mapper"})
@ServletComponentScan(basePackages = {"net.mingsoft"})
@EnableCaching
public class MSApplication {
	public static void main(String[] args) {
		SpringApplication.run(MSApplication.class, args);
	}

}
