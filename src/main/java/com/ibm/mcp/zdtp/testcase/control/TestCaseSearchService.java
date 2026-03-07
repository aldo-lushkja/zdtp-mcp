package com.ibm.mcp.zdtp.testcase.control;

import java.net.URLEncoder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.entity.TargetProcessResponse;
import com.ibm.mcp.zdtp.testcase.control.TestCaseConverter;
import com.ibm.mcp.zdtp.testcase.entity.TestCaseDto;
import com.ibm.mcp.zdtp.testcase.entity.TestCase;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public class TestCaseSearchService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],CreateDate,Owner[Id,Login]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final TestCaseConverter converter;

    public TestCaseSearchService(TargetProcessProperties properties,
                                 TargetProcessHttpClient httpClient,
                                 TestCaseConverter converter) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
    }

    public List<TestCaseDto> searchTestCases(String nameQuery, String projectName,
                                             String ownerLogin, String startDate,
                                             String endDate, int take) {
        String url = buildUrl(nameQuery, projectName, ownerLogin, startDate, endDate, take);
        String body = httpClient.fetch(url);
        TargetProcessResponse<TestCase> resp = httpClient.parse(body, new TypeReference<>() {});
        return Optional.ofNullable(resp.items()).orElse(List.of())
                .stream().map(converter::toDto).toList();
    }

    private String buildUrl(String nameQuery, String projectName,
                            String ownerLogin, String startDate, String endDate, int take) {
        String where = buildWhere(nameQuery, projectName, ownerLogin, startDate, endDate);
        return assembleUrl(where, take);
    }

    private String buildWhere(String nameQuery, String projectName,
                              String ownerLogin, String startDate, String endDate) {
        List<String> conditions = new ArrayList<>();
        if (nameQuery != null && !nameQuery.isBlank()) {
            conditions.add("Name contains '%s'".formatted(nameQuery));
        }
        if (projectName != null && !projectName.isBlank()) {
            conditions.add("Project.Name contains '%s'".formatted(projectName));
        }
        if (ownerLogin != null && !ownerLogin.isBlank()) {
            conditions.add("Owner.Login eq '%s'".formatted(ownerLogin));
        }
        if (startDate != null && !startDate.isBlank()) {
            conditions.add("CreateDate gte '%s'".formatted(startDate));
        }
        if (endDate != null && !endDate.isBlank()) {
            conditions.add("CreateDate lt '%s'".formatted(endDate));
        }
        return String.join(" and ", conditions);
    }

    private String assembleUrl(String where, int take) {
        return properties.baseUrl() + "/api/v1/TestCases"
                + "?where=" + URLEncoder.encode(where, StandardCharsets.UTF_8).replace("+", "%20")
                + "&include=" + URLEncoder.encode(INCLUDE, StandardCharsets.UTF_8).replace("+", "%20")
                + "&orderByDesc=CreateDate"
                + "&take=" + take
                + "&format=json"
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
    }
}