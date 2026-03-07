package com.ibm.mcp.zdtp.testcase.control;

import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.testcase.entity.TestCase;
import com.ibm.mcp.zdtp.testcase.entity.TestCaseDto;

public class TestCaseCreateService extends BaseService {
    private final TestCaseConverter converter;

    public TestCaseCreateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, TestCaseConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient, objectMapper);
        this.converter = converter;
    }

    public TestCaseDto createTestCase(String name, int projectId, String description, Integer testPlanId) {
        return create(name, projectId, description, testPlanId);
    }

    public TestCaseDto create(String name, int projectId, String description, Integer testPlanId) {
        Map<String, Object> body = new LinkedHashMap<>(); body.put("Name", name); body.put("Project", Map.of("Id", projectId));
        if (description != null && !description.isBlank()) body.put("Description", description);
        if (testPlanId != null) body.put("TestPlan", Map.of("Id", testPlanId));
        return engine.create(QueryEngine.TEST_CASE, body, converter::toDto, TestCase.class);
    }
}
