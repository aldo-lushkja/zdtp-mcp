package com.ibm.mcp.targetprocess.testplan.service;

import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.testplan.converter.TestPlanConverter;
import com.ibm.mcp.targetprocess.testplan.dto.TestPlanDto;
import com.ibm.mcp.targetprocess.testplan.model.TestPlan;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Service
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
                + "&include=" + UriUtils.encodeQueryParam(INCLUDE, StandardCharsets.UTF_8)
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
    }
}
