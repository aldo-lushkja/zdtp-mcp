package com.ibm.mcp.zdtp.shared.config;

import java.util.Optional;

public record TargetProcessProperties(
    String baseUrl,
    String accessToken
) {
    public static TargetProcessProperties fromEnv() {
        return new TargetProcessProperties(
            Optional.ofNullable(System.getenv("TP_URL")).orElse(""),
            Optional.ofNullable(System.getenv("TP_TOKEN")).orElse("")
        );
    }
}
