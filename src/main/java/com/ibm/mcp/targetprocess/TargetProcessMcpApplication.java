package com.ibm.mcp.targetprocess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class TargetProcessMcpApplication {
    public static void main(String[] args) {
        SpringApplication.run(TargetProcessMcpApplication.class, args);
    }
}
