package com.ibm.mcp.zdtp.testplan.control;

import java.net.URLEncoder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.entity.TargetProcessResponse;
import com.ibm.mcp.zdtp.testplan.control.TestPlanConverter;
import com.ibm.mcp.zdtp.testplan.entity.TestPlanDto;
import com.ibm.mcp.zdtp.testplan.entity.TestPlan;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public class TestPlanSearchService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,Owner[Id,Login]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final TestPlanConverter converter;

    public TestPlanSearchService(TargetProcessProperties properties,
                                 TargetProcessHttpClient httpClient,
                                 TestPlanConverter converter) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
    }

    public List<TestPlanDto> searchTestPlans(String nameQuery, String projectName,
                                             String ownerLogin, String startDate,
                                             String endDate, int take) {
        String url = buildUrl(nameQuery, projectName, ownerLogin, startDate, endDate, take);
        String body = httpClient.fetch(url);
        TargetProcessResponse<TestPlan> resp = httpClient.parse(body, new TypeReference<>() {});
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
        return properties.baseUrl() + "/api/v1/TestPlans"
                + "?where=" + URLEncoder.encode(where, StandardCharsets.UTF_8).replace("+", "%20")
                + "&include=" + URLEncoder.encode(INCLUDE, StandardCharsets.UTF_8).replace("+", "%20")
                + "&orderByDesc=CreateDate"
                + "&take=" + take
                + "&format=json"
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
    }
}