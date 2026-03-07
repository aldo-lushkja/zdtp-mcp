package com.ibm.mcp.zdtp.release.control;

import java.net.URLEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.release.control.ReleaseConverter;
import com.ibm.mcp.zdtp.release.entity.ReleaseDto;
import com.ibm.mcp.zdtp.release.entity.Release;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.control.TargetProcessClientException;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
public class ReleaseUpdateService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,StartDate,EndDate,Effort,Owner[Id,Login]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final ReleaseConverter converter;
    private final ObjectMapper objectMapper;

    public ReleaseUpdateService(TargetProcessProperties properties,
                                TargetProcessHttpClient httpClient,
                                ReleaseConverter converter,
                                ObjectMapper objectMapper) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    public ReleaseDto updateRelease(int id, String name, String description, String stateName, Double effort) {
        String url = buildUrl(id);
        String body = buildBody(name, description, stateName, effort);
        String response = httpClient.post(url, body);
        Release release = httpClient.parseSingle(response, Release.class);
        return converter.toDto(release);
    }

    private String buildUrl(int id) {
        return properties.baseUrl() + "/api/v1/Releases/" + id
                + "?format=json"
                + "&include=" + URLEncoder.encode(INCLUDE, StandardCharsets.UTF_8).replace("+", "%20")
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
    }

    private String buildBody(String name, String description, String stateName, Double effort) {
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            if (name != null && !name.isBlank()) {
                body.put("Name", name);
            }
            if (description != null && !description.isBlank()) {
                body.put("Description", description);
            }
            if (stateName != null && !stateName.isBlank()) {
                body.put("EntityState", Map.of("Name", stateName));
            }
            if (effort != null) {
                body.put("Effort", effort);
            }
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new TargetProcessClientException("Failed to serialize update request body", e);
        }
    }
}
