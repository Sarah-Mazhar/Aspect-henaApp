package com.example.hena;

import com.example.hena.admin.service.AdminService; // 👈 import your service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HenaApplication {

	public static void main(String[] args) {
		SpringApplication.run(HenaApplication.class, args);
	}

	// This runs immediately after the app starts
	@Bean
	CommandLineRunner run(AdminService adminService) {
		return args -> {
			System.out.println("Calling AdminService.getAllAdmins()");
			adminService.getAllAdmins();  // 👈 this triggers the LoggingAspect
		};
	}
}
