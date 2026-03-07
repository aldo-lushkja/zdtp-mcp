package com.ibm.mcp.zdtp.release.control;

import java.net.URLEncoder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.release.control.ReleaseConverter;
import com.ibm.mcp.zdtp.release.entity.ReleaseDto;
import com.ibm.mcp.zdtp.release.entity.Release;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.entity.TargetProcessResponse;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public class ReleaseSearchService {

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final ReleaseConverter converter;

    public ReleaseSearchService(TargetProcessProperties properties,
                                TargetProcessHttpClient httpClient,
                                ReleaseConverter converter) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
    }

    public List<ReleaseDto> searchReleases(String nameQuery, String projectName,
                                           String ownerLogin, String startDate,
                                           String endDate, int take, Integer teamIterationId) {
        String url = buildUrl(nameQuery, projectName, ownerLogin, startDate, endDate, take, teamIterationId);
        String body = httpClient.fetch(url);
        TargetProcessResponse<Release> resp = httpClient.parse(body, new TypeReference<>() {});
        return Optional.ofNullable(resp.items()).orElse(List.of())
                .stream().map(converter::toDto).toList();
    }

    private String buildUrl(String nameQuery, String projectName,
                            String ownerLogin, String startDate, String endDate, int take,
                            Integer teamIterationId) {
        String where = buildWhere(nameQuery, projectName, ownerLogin, startDate, endDate, teamIterationId);
        return assembleUrl(where, take);
    }

    private String buildWhere(String nameQuery, String projectName,
                              String ownerLogin, String startDate, String endDate,
                              Integer teamIterationId) {
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
            conditions.add("StartDate gte '%s'".formatted(startDate));
        }
        if (endDate != null && !endDate.isBlank()) {
            conditions.add("StartDate lt '%s'".formatted(endDate));
        }
        if (teamIterationId != null) {
            conditions.add("TeamIterations.Id eq %d".formatted(teamIterationId));
        }
        return String.join(" and ", conditions);
    }

    private String assembleUrl(String where, int take) {
        String include = "[Id,Name,Description,Project[Id,Name],CreateDate,StartDate,EndDate,Effort,Owner[Id,Login]]";
        return properties.baseUrl() + "/api/v1/Releases"
                + "?where=" + URLEncoder.encode(where, StandardCharsets.UTF_8).replace("+", "%20")
                + "&include=" + URLEncoder.encode(include, StandardCharsets.UTF_8).replace("+", "%20")
                + "&orderByDesc=CreateDate"
                + "&take=" + take
                + "&format=json"
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
    }
}
