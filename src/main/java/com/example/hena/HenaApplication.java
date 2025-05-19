package com.example.hena;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class HenaApplication {
	public static void main(String[] args) {
		SpringApplication.run(HenaApplication.class, args);
	}
}