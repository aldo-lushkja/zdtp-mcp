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

    public TestCaseCreateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, TestCaseConverter converter, ObjectMapper mapper) {
        super(properties, httpClient, mapper);
        this.converter = converter;
    }

    public TestCaseDto createTestCase(String name, int projectId, String description, Integer testPlanId) {
        return create(name, projectId, description, testPlanId);
    }

    public TestCaseDto create(String name, int projectId, String description, Integer testPlanId) {
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("Name", name);
        bodyMap.put("Project", Map.of("Id", projectId));
        
        if (description != null && !description.isBlank()) {
            bodyMap.put("Description", description);
        }
        
        if (testPlanId != null) {
            bodyMap.put("TestPlan", Map.of("Id", testPlanId));
        }
        
        return engine.create(QueryEngine.TEST_CASE, bodyMap, converter::toDto, TestCase.class);
    }
}
