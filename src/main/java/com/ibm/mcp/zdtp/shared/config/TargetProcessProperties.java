package com.ibm.mcp.zdtp.shared.config;

import java.util.Optional;

public record TargetProcessProperties(
        String baseUrl,
        String accessToken,
        int timeoutSeconds,
        int maxRetries,
        boolean debug
) {
    public TargetProcessProperties(String baseUrl, String accessToken) {
        this(baseUrl, accessToken, 30, 3, false);
    }

    public static TargetProcessProperties fromEnv() {
        String url = Optional.ofNullable(System.getenv("TP_URL")).orElse("");
        String token = Optional.ofNullable(System.getenv("TP_TOKEN")).orElse("");
        int timeout = parseEnvInt("TP_TIMEOUT_SECONDS", 30);
        int retries = parseEnvInt("TP_MAX_RETRIES", 3);
        boolean debug = parseEnvBool("TP_DEBUG", false);

        return new TargetProcessProperties(url, token, timeout, retries, debug);
    }

    private static int parseEnvInt(String key, int defaultValue) {
        String val = System.getenv(key);
        if (val == null || val.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static boolean parseEnvBool(String key, boolean defaultValue) {
        String val = System.getenv(key);
        if (val == null || val.isBlank()) return defaultValue;
        return Boolean.parseBoolean(val.trim()) || "1".equals(val.trim());
    }
}
