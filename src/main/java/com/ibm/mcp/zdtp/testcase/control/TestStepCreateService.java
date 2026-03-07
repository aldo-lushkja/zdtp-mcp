package com.ibm.mcp.zdtp.testcase.control;

import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.testcase.entity.TestStep;
import com.ibm.mcp.zdtp.testcase.entity.TestStepDto;

public class TestStepCreateService extends BaseService {
    private final TestStepConverter converter;

    public TestStepCreateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, TestStepConverter converter, ObjectMapper mapper) {
        super(properties, httpClient, mapper);
        this.converter = converter;
    }

    public TestStepDto create(int testCaseId, String description, String expectedResult, Integer runOrder) {
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("TestCase", Map.of("Id", testCaseId));
        bodyMap.put("Description", description);
        if (expectedResult != null) {
            bodyMap.put("Result", expectedResult);
        }
        if (runOrder != null) {
            bodyMap.put("RunOrder", runOrder);
        }
        
        return engine.create(QueryEngine.TEST_STEP, bodyMap, converter::toDto, TestStep.class);
    }
}
