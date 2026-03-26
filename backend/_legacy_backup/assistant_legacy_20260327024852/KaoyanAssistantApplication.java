package com.kaoyan.assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class KaoyanAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(KaoyanAssistantApplication.class, args);
    }
}
