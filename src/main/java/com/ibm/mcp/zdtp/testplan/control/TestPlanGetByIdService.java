package com.ibm.mcp.zdtp.testplan.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.testplan.entity.TestPlan;
import com.ibm.mcp.zdtp.testplan.entity.TestPlanDto;

public class TestPlanGetByIdService extends BaseService {
    private final TestPlanConverter converter;

    public TestPlanGetByIdService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, TestPlanConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient, objectMapper);
        this.converter = converter;
    }

    public TestPlanDto getById(int id) {
        return get(id);
    }

    public TestPlanDto get(int id) {
        return engine.get(QueryEngine.TEST_PLAN, id, converter::toDto, TestPlan.class);
    }
}
