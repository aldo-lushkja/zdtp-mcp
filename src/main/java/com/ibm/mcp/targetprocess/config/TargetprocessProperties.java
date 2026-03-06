package com.ibm.mcp.targetprocess.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "targetprocess")
public record TargetprocessProperties(
    String baseUrl,
    String accessToken
) {}
