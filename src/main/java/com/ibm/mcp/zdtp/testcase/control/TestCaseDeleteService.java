package com.ibm.mcp.zdtp.testcase.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

public class TestCaseDeleteService extends BaseService {
    public TestCaseDeleteService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, ObjectMapper mapper) {
        super(properties, httpClient, mapper);
    }

    public void delete(int id) {
        engine.delete(QueryEngine.TEST_CASE, id);
    }
}
