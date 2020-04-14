package com.testTask.widgetsService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.testTask.widgetsService", "com.testTask.storage", "com.testTask.domain", "com.testTask.widgetLogic"})
public class WidgetsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WidgetsServiceApplication.class, args);
	}
	
}