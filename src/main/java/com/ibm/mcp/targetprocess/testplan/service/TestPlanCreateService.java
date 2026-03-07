package com.ibm.mcp.targetprocess.testplan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.shared.exception.TargetProcessClientException;
import com.ibm.mcp.targetprocess.testplan.converter.TestPlanConverter;
import com.ibm.mcp.targetprocess.testplan.dto.TestPlanDto;
import com.ibm.mcp.targetprocess.testplan.model.TestPlan;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TestPlanCreateService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,Owner[Id,Login]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final TestPlanConverter converter;
    private final ObjectMapper objectMapper;

    public TestPlanCreateService(TargetProcessProperties properties,
                                 TargetProcessHttpClient httpClient,
                                 TestPlanConverter converter,
                                 ObjectMapper objectMapper) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    public TestPlanDto createTestPlan(String name, int projectId, String description) {
        String url = buildUrl();
        String body = buildBody(name, projectId, description);
        String response = httpClient.post(url, body);
        TestPlan testPlan = httpClient.parseSingle(response, TestPlan.class);
        return converter.toDto(testPlan);
    }

    private String buildUrl() {
        return properties.baseUrl() + "/api/v1/TestPlans"
                + "?format=json"
                + "&include=" + UriUtils.encodeQueryParam(INCLUDE, StandardCharsets.UTF_8)
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
    }

    private String buildBody(String name, int projectId, String description) {
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Name", name);
            body.put("Project", Map.of("Id", projectId));
            if (description != null && !description.isBlank()) {
                body.put("Description", description);
            }
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new TargetProcessClientException("Failed to serialize create request body", e);
        }
    }
}