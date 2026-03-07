package com.ibm.mcp.zdtp.testplan.control;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.*;
import com.ibm.mcp.zdtp.testplan.entity.TestPlan;
import com.ibm.mcp.zdtp.testplan.entity.TestPlanDto;

public class TestPlanCreateService extends BaseService {
    private static final String INCLUDE = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,Owner[Id,Login]]";
    private final TestPlanConverter converter;
    private final ObjectMapper objectMapper;

    public TestPlanCreateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, TestPlanConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient);
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    public TestPlanDto createTestPlan(String name, int projectId, String description) {
        return create(name, projectId, description);
    }

    public TestPlanDto create(String name, int projectId, String description) {
        try {
            Map<String, Object> bodyMap = new LinkedHashMap<>();
            bodyMap.put("Name", name);
            bodyMap.put("Project", Map.of("Id", projectId));
            if (description != null && !description.isBlank()) {
                bodyMap.put("Description", description);
            }
            
            String jsonBody = objectMapper.writeValueAsString(bodyMap);
            Map<String, String> parameters = new TreeMap<>();
            parameters.put("include", INCLUDE);
            return postSingle("TestPlans", parameters, jsonBody, TestPlan.class, converter::toDto);
        } catch (Exception e) {
            if (e instanceof TargetProcessApiException) throw (TargetProcessApiException) e;
            throw new TargetProcessClientException("Failed to serialize create request body", e);
        }
    }
}
