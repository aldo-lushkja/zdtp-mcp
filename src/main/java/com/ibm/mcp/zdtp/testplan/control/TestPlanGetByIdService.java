package com.ibm.mcp.zdtp.testplan.control;


import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.testplan.entity.TestPlan;
import com.ibm.mcp.zdtp.testplan.entity.TestPlanDto;

public class TestPlanGetByIdService extends BaseService {
    private final TestPlanConverter converter;

    public TestPlanGetByIdService(QueryEngine engine, TestPlanConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public TestPlanDto getById(int id) {
        return get(id);
    }

    public TestPlanDto get(int id) {
        return engine.get(QueryEngine.TEST_PLAN, id, converter::toDto, TestPlan.class);
    }
}

