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

public class TestPlanUpdateService extends BaseService {
    private final TestPlanConverter converter;

    public TestPlanUpdateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, TestPlanConverter converter, ObjectMapper mapper) {
        super(properties, httpClient, mapper);
        this.converter = converter;
    }

    public TestPlanDto updateTestPlan(int id, String name, String description, String stateName) {
        return update(id, name, description, stateName);
    }

    public TestPlanDto update(int id, String name, String description, String stateName) {
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        if (name != null && !name.isBlank()) {
            bodyMap.put("Name", name);
        }
        if (description != null) {
            bodyMap.put("Description", description);
        }
        if (stateName != null && !stateName.isBlank()) {
            bodyMap.put("EntityState", Map.of("Name", stateName));
        }
        
        return engine.update(QueryEngine.TEST_PLAN, id, bodyMap, converter::toDto, TestPlan.class);
    }
}
