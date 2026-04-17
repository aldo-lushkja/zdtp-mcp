package com.ibm.mcp.zdtp.testplan.control;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.testplan.entity.TestPlan;
import com.ibm.mcp.zdtp.testplan.entity.TestPlanDto;

public class TestPlanUpdateService extends BaseService {
    private final TestPlanConverter converter;

    public TestPlanUpdateService(QueryEngine engine, TestPlanConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public TestPlanDto updateTestPlan(int id, String name, String description, String stateName) {
        return update(id, name, description, stateName, null);
    }

    public TestPlanDto update(int id, String name, String description, String stateName, Integer stateId) {
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        if (name != null && !name.isBlank()) {
            bodyMap.put("Name", name);
        }
        if (description != null) {
            bodyMap.put("Description", convertMarkdown(description));
        }
        if (stateId != null && stateId > 0) {
            bodyMap.put("EntityState", Map.of("Id", stateId));
        } else if (stateName != null && !stateName.isBlank()) {
            bodyMap.put("EntityState", Map.of("Name", stateName));
        }
        
        return engine.update(QueryEngine.TEST_PLAN, id, bodyMap, converter::toDto, TestPlan.class);
    }
}


