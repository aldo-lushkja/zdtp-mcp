package com.ibm.mcp.zdtp.shared.control;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ibm.mcp.zdtp.shared.entity.TargetProcessResponse;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class TargetProcessHttpClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    public TargetProcessHttpClient(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public String fetch(String url) {
        return send(HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build()).body();
    }

    public String post(String url, String jsonBody) {
        return send(HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build()).body();
    }

    public void delete(String url) {
        send(HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30))
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
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new TargetProcessApiException(response.statusCode(), response.body());
            }
            return response;
        } catch (TargetProcessApiException e) {
            throw e;
        } catch (Exception e) {
            throw new TargetProcessClientException("Failed to call Targetprocess API", e);
        }
    }
}
