package com.ibm.mcp.targetprocess.shared.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.targetprocess.shared.exception.TargetProcessApiException;
import com.ibm.mcp.targetprocess.shared.exception.TargetProcessClientException;
import com.ibm.mcp.targetprocess.shared.model.TargetProcessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class TargetProcessHttpClient {

    private static final Logger log = LoggerFactory.getLogger(TargetProcessHttpClient.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public TargetProcessHttpClient(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public String fetch(String url) {
        HttpRequest request = buildGetRequest(url);
        HttpResponse<String> response = send(request);
        validateResponse(response);
        return response.body();
    }

    public String post(String url, String jsonBody) {
        log.debug("POST body={}", jsonBody);
        HttpRequest request = buildPostRequest(url, jsonBody);
        HttpResponse<String> response = send(request);
        validateResponse(response);
        return response.body();
    }

    public <T> TargetProcessResponse<T> parse(String body, TypeReference<TargetProcessResponse<T>> ref) {
        try {
            return objectMapper.readValue(body, ref);
        } catch (Exception e) {
            throw new TargetProcessClientException("Failed to parse Targetprocess response", e);
        }
    }

    public <T> T parseSingle(String body, Class<T> clazz) {
        try {
            return objectMapper.readValue(body, clazz);
        } catch (Exception e) {
            throw new TargetProcessClientException("Failed to parse Targetprocess response", e);
        }
    }

    private HttpRequest buildGetRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();
    }

    private HttpRequest buildPostRequest(String url, String jsonBody) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
    }

    private HttpResponse<String> send(HttpRequest request) {
        log.debug("{} {}", request.method(), request.uri());
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.debug("Response status={} body={}", response.statusCode(), response.body());
            return response;
        } catch (Exception e) {
            throw new TargetProcessClientException("Failed to call Targetprocess API", e);
        }
    }

    private void validateResponse(HttpResponse<String> response) {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new TargetProcessApiException(response.statusCode(), response.body());
        }
    }
}
