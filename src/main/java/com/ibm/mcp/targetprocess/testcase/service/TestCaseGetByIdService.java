package com.ibm.mcp.targetprocess.testcase.service;

import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.testcase.converter.TestCaseConverter;
import com.ibm.mcp.targetprocess.testcase.dto.TestCaseDto;
import com.ibm.mcp.targetprocess.testcase.model.TestCase;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Service
public class TestCaseGetByIdService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,Owner[Id,Login]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final TestCaseConverter converter;

    public TestCaseGetByIdService(TargetProcessProperties properties,
                                  TargetProcessHttpClient httpClient,
                                  TestCaseConverter converter) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
    }

    public TestCaseDto getById(int id) {
        String url = buildUrl(id);
        String response = httpClient.fetch(url);
        TestCase testCase = httpClient.parseSingle(response, TestCase.class);
        return converter.toDto(testCase);
    }

    private String buildUrl(int id) {
        return properties.baseUrl() + "/api/v1/TestCases/" + id
                + "?format=json"
                + "&include=" + UriUtils.encodeQueryParam(INCLUDE, StandardCharsets.UTF_8)
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
    }
}
