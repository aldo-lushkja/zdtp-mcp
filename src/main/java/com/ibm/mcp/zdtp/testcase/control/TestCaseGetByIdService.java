package com.ibm.mcp.zdtp.testcase.control;

import java.util.Map;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.testcase.entity.TestCase;
import com.ibm.mcp.zdtp.testcase.entity.TestCaseDto;

public class TestCaseGetByIdService extends BaseService {
    private static final String INCLUDE = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,Owner[Id,Login],TestPlan[Id,Name]]";
    private final TestCaseConverter converter;

    public TestCaseGetByIdService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, TestCaseConverter converter) {
        super(properties, httpClient);
        this.converter = converter;
    }

    public TestCaseDto getById(int id) {
        return get(id);
    }

    public TestCaseDto get(int id) {
        Map<String, String> parameters = Map.of("include", INCLUDE);
        return fetchSingle("TestCases/" + id, parameters, TestCase.class, converter::toDto);
    }
}
