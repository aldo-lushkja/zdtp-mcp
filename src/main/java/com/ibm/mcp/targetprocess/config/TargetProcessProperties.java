package com.ibm.mcp.targetprocess.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ConfigurationProperties(prefix = "targetprocess")
public record TargetProcessProperties(
    String baseUrl,
    String accessToken
) {}
