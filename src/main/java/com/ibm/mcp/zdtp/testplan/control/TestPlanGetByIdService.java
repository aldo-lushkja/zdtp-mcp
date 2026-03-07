package com.ibm.mcp.zdtp.testplan.control;

import java.net.URLEncoder;

import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.testplan.control.TestPlanConverter;
import com.ibm.mcp.zdtp.testplan.entity.TestPlanDto;
import com.ibm.mcp.zdtp.testplan.entity.TestPlan;

import java.nio.charset.StandardCharsets;
public class TestPlanGetByIdService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,Owner[Id,Login]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final TestPlanConverter converter;

    public TestPlanGetByIdService(TargetProcessProperties properties,
                                  TargetProcessHttpClient httpClient,
                                  TestPlanConverter converter) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
    }

    public TestPlanDto getById(int id) {
        String url = buildUrl(id);
        String response = httpClient.fetch(url);
        TestPlan testPlan = httpClient.parseSingle(response, TestPlan.class);
        return converter.toDto(testPlan);
    }

    private String buildUrl(int id) {
        return properties.baseUrl() + "/api/v1/TestPlans/" + id
                + "?format=json"
                + "&include=" + URLEncoder.encode(INCLUDE, StandardCharsets.UTF_8).replace("+", "%20")
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
    }
}
