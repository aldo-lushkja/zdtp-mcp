package com.ibm.mcp.zdtp.shared.config;

import java.util.Objects;

public record TargetProcessProperties(
    String baseUrl,
    String accessToken
) {
    public static TargetProcessProperties fromEnv() {
        return new TargetProcessProperties(
            Objects.requireNonNullElse(System.getenv("TP_URL"), ""),
            Objects.requireNonNullElse(System.getenv("TP_TOKEN"), "")
        );
    }
}
