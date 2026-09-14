package com.ibm.mcp.zdtp.shared.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.exception.TargetProcessApiException;
import com.ibm.mcp.zdtp.shared.exception.TargetProcessClientException;
import com.ibm.mcp.zdtp.shared.model.TargetProcessResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Random;

public class TargetProcessHttpClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final TargetProcessProperties properties;

    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final Random RANDOM = new Random();

    public TargetProcessHttpClient(HttpClient httpClient, ObjectMapper objectMapper, TargetProcessProperties properties) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.properties = properties != null ? properties : TargetProcessProperties.fromEnv();
    }

    public TargetProcessHttpClient(HttpClient httpClient, ObjectMapper objectMapper) {
        this(httpClient, objectMapper, TargetProcessProperties.fromEnv());
    }

    public String fetch(String url) {
        int timeout = properties != null ? properties.timeoutSeconds() : 30;
        return send(HttpRequest.newBuilder()
                .uri(URI.create(url.trim()))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(timeout))
                .GET()
                .build()).body();
    }

    public String post(String url, String jsonBody) {
        int timeout = properties != null ? properties.timeoutSeconds() : 30;
        return send(HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(timeout))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build()).body();
    }

    public void delete(String url) {
        int timeout = properties != null ? properties.timeoutSeconds() : 30;
        send(HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(timeout))
                .DELETE()
                .build());
    }

    public <T> TargetProcessResponse<T> parse(String body, TypeReference<TargetProcessResponse<T>> ref) {
        try {
            return getMapper().readValue(body, ref);
        } catch (Exception e) {
            throw new TargetProcessClientException("Failed to parse Targetprocess response", e);
        }
    }

    public <T> T parseSingle(String body, Class<T> clazz) {
        try {
            return getMapper().readValue(body, clazz);
        } catch (Exception e) {
            throw new TargetProcessClientException("Failed to parse Targetprocess response", e);
        }
    }

    private ObjectMapper getMapper() {
        return objectMapper != null ? objectMapper : DEFAULT_MAPPER;
    }

    public static String encode(String value) {
        if (value == null) return "";
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private HttpResponse<String> send(HttpRequest request) {
        int maxRetries = properties != null ? properties.maxRetries() : 3;
        boolean debug = properties != null && properties.debug();
        long startTime = System.currentTimeMillis();

        Exception lastException = null;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                if (debug) {
                    System.err.printf("[TP-HTTP DEBUG] [%s] %s (Attempt %d/%d)%n",
                            request.method(), request.uri(), attempt, maxRetries);
                }

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                int statusCode = response.statusCode();

                if (debug) {
                    System.err.printf("[TP-HTTP DEBUG] Response Status: %d (%d ms)%n",
                            statusCode, System.currentTimeMillis() - startTime);
                }

                if (statusCode >= 200 && statusCode < 300) {
                    return response;
                }

                // Check for transient status codes (429 Rate Limit, 502 Bad Gateway, 503 Service Unavailable, 504 Gateway Timeout)
                if (isTransientStatus(statusCode) && attempt < maxRetries) {
                    backoff(attempt, debug);
                    continue;
                }

                throw new TargetProcessApiException(statusCode, response.body());

            } catch (TargetProcessApiException e) {
                throw e;
            } catch (IOException e) {
                lastException = e;
                if (attempt < maxRetries) {
                    backoff(attempt, debug);
                } else {
                    throw new TargetProcessClientException("Failed to call Targetprocess API after " + maxRetries + " attempts", e);
                }
            } catch (Exception e) {
                throw new TargetProcessClientException("Failed to call Targetprocess API", e);
            }
        }

        throw new TargetProcessClientException("Failed to call Targetprocess API", lastException);
    }

    private boolean isTransientStatus(int statusCode) {
        return statusCode == 429 || statusCode == 502 || statusCode == 503 || statusCode == 504;
    }

    private void backoff(int attempt, boolean debug) {
        long delay = (long) (Math.pow(2, attempt) * 100) + RANDOM.nextInt(50);
        if (debug) {
            System.err.printf("[TP-HTTP DEBUG] Retrying in %d ms...%n", delay);
        }
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
