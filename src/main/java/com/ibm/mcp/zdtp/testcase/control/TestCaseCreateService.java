package com.ibm.mcp.zdtp.testcase.control;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.*;
import com.ibm.mcp.zdtp.testcase.entity.TestCase;
import com.ibm.mcp.zdtp.testcase.entity.TestCaseDto;

public class TestCaseCreateService extends BaseService {
    private static final String INCLUDE = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,Owner[Id,Login],TestPlan[Id,Name]]";
    private final TestCaseConverter converter;
    private final ObjectMapper objectMapper;

    public TestCaseCreateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, TestCaseConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient);
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    public TestCaseDto createTestCase(String name, int projectId, String description, Integer testPlanId) {
        return create(name, projectId, description, testPlanId);
    }

    public TestCaseDto create(String name, int projectId, String description, Integer testPlanId) {
        try {
            Map<String, Object> bodyMap = new LinkedHashMap<>();
            bodyMap.put("Name", name);
            bodyMap.put("Project", Map.of("Id", projectId));
            if (description != null && !description.isBlank()) {
                bodyMap.put("Description", description);
            }
            if (testPlanId != null) {
                bodyMap.put("TestPlan", Map.of("Id", testPlanId));
            }
            
            String jsonBody = objectMapper.writeValueAsString(bodyMap);
            Map<String, String> parameters = new TreeMap<>();
            parameters.put("include", INCLUDE);
            return postSingle("TestCases", parameters, jsonBody, TestCase.class, converter::toDto);
        } catch (Exception e) {
            if (e instanceof TargetProcessApiException) throw (TargetProcessApiException) e;
            throw new TargetProcessClientException("Failed to serialize create request body", e);
        }
    }
}
