package com.ibm.mcp.zdtp.testplan.control;

import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.testplan.entity.TestPlan;
import com.ibm.mcp.zdtp.testplan.entity.TestPlanDto;

public class TestPlanCreateService extends BaseService {
    private final TestPlanConverter converter;

    public TestPlanCreateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, TestPlanConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient, objectMapper);
        this.converter = converter;
    }

    public TestPlanDto createTestPlan(String name, int projectId, String description) {
        return create(name, projectId, description);
    }

    public TestPlanDto create(String name, int projectId, String description) {
        Map<String, Object> body = new LinkedHashMap<>(); body.put("Name", name); body.put("Project", Map.of("Id", projectId));
        if (description != null && !description.isBlank()) body.put("Description", description);
        return engine.create(QueryEngine.TEST_PLAN, body, converter::toDto, TestPlan.class);
    }
}
