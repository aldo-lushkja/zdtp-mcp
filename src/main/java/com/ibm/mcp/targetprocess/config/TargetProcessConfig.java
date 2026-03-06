package com.ibm.mcp.targetprocess.config;

import com.ibm.mcp.targetprocess.userstory.controller.UserStoryMcpTools;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@EnableConfigurationProperties(TargetProcessProperties.class)
public class TargetProcessConfig {

    @Bean
    public HttpClient targetprocessHttpClient() {
        return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();
    }

    @Bean
    public ToolCallbackProvider targetprocessTools(UserStoryMcpTools mcpTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(mcpTools)
                .build();
    }
}
