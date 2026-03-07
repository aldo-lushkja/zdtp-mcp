package com.ibm.mcp.zdtp.testcase.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.testcase.entity.TestCase;
import com.ibm.mcp.zdtp.testcase.entity.TestCaseDto;

public class TestCaseGetByIdService extends BaseService {
    private final TestCaseConverter converter;

    public TestCaseGetByIdService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, TestCaseConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient, objectMapper);
        this.converter = converter;
    }

    public TestCaseDto getById(int id) {
        return get(id);
    }

    public TestCaseDto get(int id) {
        return engine.get(QueryEngine.TEST_CASE, id, converter::toDto, TestCase.class);
    }
}
