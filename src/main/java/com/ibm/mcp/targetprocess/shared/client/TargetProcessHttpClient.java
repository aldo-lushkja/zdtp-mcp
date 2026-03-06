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
        HttpRequest request = buildRequest(url);
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

    private HttpRequest buildRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();
    }

    private HttpResponse<String> send(HttpRequest request) {
        log.debug("GET {}", request.uri());
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.debug("Response status={} body={}", response.statusCode(), response.body());
            return response;
        } catch (Exception e) {
            throw new TargetProcessClientException("Failed to call Targetprocess API", e);
        }
    }

    private void validateResponse(HttpResponse<String> response) {
        if (response.statusCode() != 200) {
            throw new TargetProcessApiException(response.statusCode(), response.body());
        }
    }
}
