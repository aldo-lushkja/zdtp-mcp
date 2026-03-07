package com.ibm.mcp.zdtp.testcase.control;

import java.net.URLEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.control.TargetProcessClientException;
import com.ibm.mcp.zdtp.testcase.control.TestCaseConverter;
import com.ibm.mcp.zdtp.testcase.entity.TestCaseDto;
import com.ibm.mcp.zdtp.testcase.entity.TestCase;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class TestCaseCreateService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],CreateDate,Owner[Id,Login]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final TestCaseConverter converter;
    private final ObjectMapper objectMapper;

    public TestCaseCreateService(TargetProcessProperties properties,
                                 TargetProcessHttpClient httpClient,
                                 TestCaseConverter converter,
                                 ObjectMapper objectMapper) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    public TestCaseDto createTestCase(String name, int projectId, String description, Integer testPlanId) {
        String url = buildUrl();
        String body = buildBody(name, projectId, description, testPlanId);
        String response = httpClient.post(url, body);
        TestCase testCase = httpClient.parseSingle(response, TestCase.class);
        return converter.toDto(testCase);
    }

    private String buildUrl() {
        return properties.baseUrl() + "/api/v1/TestCases"
                + "?format=json"
                + "&include=" + URLEncoder.encode(INCLUDE, StandardCharsets.UTF_8).replace("+", "%20")
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
    }

    private String buildBody(String name, int projectId, String description, Integer testPlanId) {
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Name", name);
            body.put("Project", Map.of("Id", projectId));
            if (description != null && !description.isBlank()) {
                body.put("Description", description);
            }
            if (testPlanId != null && testPlanId > 0) {
                body.put("TestPlans", List.of(Map.of("Id", testPlanId)));
            }
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new TargetProcessClientException("Failed to serialize create request body", e);
        }
    }
}