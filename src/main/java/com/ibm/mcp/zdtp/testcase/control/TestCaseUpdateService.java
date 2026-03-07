package com.ibm.mcp.zdtp.testcase.control;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.*;
import com.ibm.mcp.zdtp.testcase.entity.TestCase;
import com.ibm.mcp.zdtp.testcase.entity.TestCaseDto;

public class TestCaseUpdateService extends BaseService {
    private static final String INCLUDE = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,Owner[Id,Login],TestPlan[Id,Name]]";
    private final TestCaseConverter converter;
    private final ObjectMapper objectMapper;

    public TestCaseUpdateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, TestCaseConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient);
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    public TestCaseDto updateTestCase(int id, String name, String description, String stateName) {
        return update(id, name, description, stateName);
    }

    public TestCaseDto update(int id, String name, String description, String stateName) {
        try {
            Map<String, Object> bodyMap = new LinkedHashMap<>();
            if (name != null && !name.isBlank()) bodyMap.put("Name", name);
            if (description != null) bodyMap.put("Description", description);
            if (stateName != null && !stateName.isBlank()) bodyMap.put("EntityState", Map.of("Name", stateName));
            
            String jsonBody = bodyMap.isEmpty() ? "{}" : objectMapper.writeValueAsString(bodyMap);
            Map<String, String> parameters = new TreeMap<>();
            parameters.put("include", INCLUDE);
            return postSingle("TestCases/" + id, parameters, jsonBody, TestCase.class, converter::toDto);
        } catch (Exception e) {
            if (e instanceof TargetProcessApiException) throw (TargetProcessApiException) e;
            throw new TargetProcessClientException("Failed to serialize update request body", e);
        }
    }
}
