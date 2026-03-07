package com.ibm.mcp.zdtp.testplan.control;

import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.*;
import com.ibm.mcp.zdtp.testplan.entity.TestPlan;
import com.ibm.mcp.zdtp.testplan.entity.TestPlanDto;

public class TestPlanUpdateService extends BaseService {
    private final TestPlanConverter converter;

    public TestPlanUpdateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, TestPlanConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient, objectMapper);
        this.converter = converter;
    }

    public TestPlanDto updateTestPlan(int id, String name, String description, String stateName) {
        return update(id, name, description, stateName);
    }

    public TestPlanDto update(int id, String name, String description, String stateName) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (name != null && !name.isBlank()) body.put("Name", name);
        if (description != null) body.put("Description", description);
        if (stateName != null && !stateName.isBlank()) body.put("EntityState", Map.of("Name", stateName));
        return engine.update(QueryEngine.TEST_PLAN, id, body, converter::toDto, TestPlan.class);
    }
}
