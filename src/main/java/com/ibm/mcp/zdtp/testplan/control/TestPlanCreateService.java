package com.ibm.mcp.zdtp.testplan.control;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.testplan.entity.TestPlan;
import com.ibm.mcp.zdtp.testplan.entity.TestPlanDto;

public class TestPlanCreateService extends BaseService {
    private final TestPlanConverter converter;

    public TestPlanCreateService(QueryEngine engine, TestPlanConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public TestPlanDto createTestPlan(String name, int projectId, String description) {
        return create(name, projectId, description);
    }

    public TestPlanDto create(String name, int projectId, String description) {
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("Name", name);
        bodyMap.put("Project", Map.of("Id", projectId));
        
        if (description != null && !description.isBlank()) {
            bodyMap.put("Description", description);
        }
        
        return engine.create(QueryEngine.TEST_PLAN, bodyMap, converter::toDto, TestPlan.class);
    }
}

