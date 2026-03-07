package com.ibm.mcp.zdtp.testcase.control;

import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.*;
import com.ibm.mcp.zdtp.testcase.entity.TestCase;
import com.ibm.mcp.zdtp.testcase.entity.TestCaseDto;

public class TestCaseUpdateService extends BaseService {
    private final TestCaseConverter converter;

    public TestCaseUpdateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, TestCaseConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient, objectMapper);
        this.converter = converter;
    }

    public TestCaseDto updateTestCase(int id, String name, String description, String stateName) {
        return update(id, name, description, stateName);
    }

    public TestCaseDto update(int id, String name, String description, String stateName) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (name != null && !name.isBlank()) body.put("Name", name);
        if (description != null) body.put("Description", description);
        if (stateName != null && !stateName.isBlank()) body.put("EntityState", Map.of("Name", stateName));
        return engine.update(QueryEngine.TEST_CASE, id, body, converter::toDto, TestCase.class);
    }
}
