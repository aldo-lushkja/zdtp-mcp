package com.ibm.mcp.zdtp.testcase.control;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.testcase.entity.TestCase;
import com.ibm.mcp.zdtp.testcase.entity.TestCaseDto;

public class TestCaseUpdateService extends BaseService {
    private final TestCaseConverter converter;

    public TestCaseUpdateService(QueryEngine engine, TestCaseConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public TestCaseDto updateTestCase(int id, String name, String description, String stateName) {
        return update(id, name, description, stateName);
    }

    public TestCaseDto update(int id, String name, String description, String stateName) {
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        if (name != null && !name.isBlank()) {
            bodyMap.put("Name", name);
        }
        if (description != null) {
            bodyMap.put("Description", convertMarkdown(description));
        }
        if (stateName != null && !stateName.isBlank()) {
            bodyMap.put("EntityState", Map.of("Name", stateName));
        }
        
        return engine.update(QueryEngine.TEST_CASE, id, bodyMap, converter::toDto, TestCase.class);
    }
}


