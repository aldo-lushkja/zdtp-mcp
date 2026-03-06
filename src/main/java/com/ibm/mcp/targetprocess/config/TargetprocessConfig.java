package com.ibm.mcp.targetprocess.config;

import com.ibm.mcp.targetprocess.controller.TargetprocessMcpTools;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(TargetprocessProperties.class)
public class TargetprocessConfig {

    @Bean
    public RestClient targetprocessRestClient(TargetprocessProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader("Accept", "application/json")
                .build();
    }

    @Bean
    public ToolCallbackProvider targetprocessTools(TargetprocessMcpTools mcpTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(mcpTools)
                .build();
    }
}
