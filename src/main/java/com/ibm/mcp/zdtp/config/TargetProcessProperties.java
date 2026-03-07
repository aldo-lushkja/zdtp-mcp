package com.ibm.mcp.zdtp.config;

import java.util.Optional;

public record TargetProcessProperties(
    String baseUrl,
    String accessToken
) {
    public static TargetProcessProperties fromEnv() {
        return new TargetProcessProperties(
            Optional.ofNullable(System.getenv("TARGETPROCESS_BASE_URL")).orElse(""),
            Optional.ofNullable(System.getenv("TARGETPROCESS_ACCESS_TOKEN")).orElse("")
        );
    }
}
