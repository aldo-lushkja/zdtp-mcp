package com.ibm.mcp.zdtp.testplan.control;

import java.net.URLEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.control.TargetProcessClientException;
import com.ibm.mcp.zdtp.testplan.control.TestPlanConverter;
import com.ibm.mcp.zdtp.testplan.entity.TestPlanDto;
import com.ibm.mcp.zdtp.testplan.entity.TestPlan;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
public class TestPlanUpdateService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,Owner[Id,Login]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final TestPlanConverter converter;
    private final ObjectMapper objectMapper;

    public TestPlanUpdateService(TargetProcessProperties properties,
                                 TargetProcessHttpClient httpClient,
                                 TestPlanConverter converter,
                                 ObjectMapper objectMapper) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    public TestPlanDto updateTestPlan(int id, String name, String description, String stateName) {
        String url = buildUrl(id);
        String body = buildBody(name, description, stateName);
        String response = httpClient.post(url, body);
        TestPlan testPlan = httpClient.parseSingle(response, TestPlan.class);
        return converter.toDto(testPlan);
    }

    private String buildUrl(int id) {
        return properties.baseUrl() + "/api/v1/TestPlans/" + id
                + "?format=json"
                + "&include=" + URLEncoder.encode(INCLUDE, StandardCharsets.UTF_8).replace("+", "%20")
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
    }

    private String buildBody(String name, String description, String stateName) {
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
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new TargetProcessClientException("Failed to serialize update request body", e);
        }
    }
}