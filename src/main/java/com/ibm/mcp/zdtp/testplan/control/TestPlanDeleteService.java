package com.ibm.mcp.zdtp.testplan.control;


import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;

public class TestPlanDeleteService extends BaseService {
    public TestPlanDeleteService(QueryEngine engine) {
        super(engine);
    }

    public void delete(int id) {
        engine.delete(QueryEngine.TEST_PLAN, id);
    }
}

