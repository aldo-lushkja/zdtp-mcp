package com.ibm.mcp.zdtp.testcase.control;

import java.net.URLEncoder;

import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.testcase.control.TestCaseConverter;
import com.ibm.mcp.zdtp.testcase.entity.TestCaseDto;
import com.ibm.mcp.zdtp.testcase.entity.TestCase;

import java.nio.charset.StandardCharsets;
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
                + "&include=" + URLEncoder.encode(INCLUDE, StandardCharsets.UTF_8).replace("+", "%20")
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
    }
}
