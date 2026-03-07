package com.ibm.mcp.targetprocess.config;

import com.ibm.mcp.targetprocess.feature.controller.FeatureMcpTools;
import com.ibm.mcp.targetprocess.request.controller.RequestMcpTools;
import com.ibm.mcp.targetprocess.testplan.controller.TestPlanMcpTools;
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
    public ToolCallbackProvider targetprocessTools(UserStoryMcpTools userStoryMcpTools,
                                                   FeatureMcpTools featureMcpTools,
                                                   TestPlanMcpTools testPlanMcpTools,
                                                   RequestMcpTools requestMcpTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(userStoryMcpTools, featureMcpTools, testPlanMcpTools, requestMcpTools)
                .build();
    }
}
