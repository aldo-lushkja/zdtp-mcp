package com.ibm.mcp.zdtp.testcase.control;


import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.testcase.entity.TestCase;
import com.ibm.mcp.zdtp.testcase.entity.TestCaseDto;

public class TestCaseGetByIdService extends BaseService {
    private final TestCaseConverter converter;

    public TestCaseGetByIdService(QueryEngine engine, TestCaseConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public TestCaseDto getById(int id) {
        return get(id);
    }

    public TestCaseDto get(int id) {
        return engine.get(QueryEngine.TEST_CASE, id, converter::toDto, TestCase.class);
    }
}

