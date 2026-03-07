package com.ibm.mcp.targetprocess.teamiteration.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.shared.model.TargetProcessResponse;
import com.ibm.mcp.targetprocess.teamiteration.converter.TeamIterationConverter;
import com.ibm.mcp.targetprocess.teamiteration.dto.TeamIterationDto;
import com.ibm.mcp.targetprocess.teamiteration.model.TeamIteration;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TeamIterationSearchService {

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final TeamIterationConverter converter;

    public TeamIterationSearchService(TargetProcessProperties properties,
                                      TargetProcessHttpClient httpClient,
                                      TeamIterationConverter converter) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
    }

    public List<TeamIterationDto> searchTeamIterations(String nameQuery, Integer teamId,
                                                        String teamName, String startDate,
                                                        String endDate, int take) {
        String url = buildUrl(nameQuery, teamId, teamName, startDate, endDate, take);
        String body = httpClient.fetch(url);
        TargetProcessResponse<TeamIteration> resp = httpClient.parse(body, new TypeReference<>() {});
        return Optional.ofNullable(resp.items()).orElse(List.of())
                .stream().map(converter::toDto).toList();
    }

    private String buildUrl(String nameQuery, Integer teamId, String teamName,
                            String startDate, String endDate, int take) {
        List<String> conditions = new ArrayList<>();
        if (nameQuery != null && !nameQuery.isBlank()) {
            conditions.add("Name contains '%s'".formatted(nameQuery));
        }
        if (teamId != null) {
            conditions.add("Team.Id eq %d".formatted(teamId));
        }
        if (teamName != null && !teamName.isBlank()) {
            conditions.add("Team.Name contains '%s'".formatted(teamName));
        }
        if (startDate != null && !startDate.isBlank()) {
            conditions.add("StartDate gte '%s'".formatted(startDate));
        }
        if (endDate != null && !endDate.isBlank()) {
            conditions.add("StartDate lt '%s'".formatted(endDate));
        }
        String where = String.join(" and ", conditions);
        String include = "[Id,Name,StartDate,EndDate,Team[Id,Name]]";
        return properties.baseUrl() + "/api/v1/TeamIterations"
                + "?where=" + UriUtils.encodeQueryParam(where, StandardCharsets.UTF_8)
                + "&include=" + UriUtils.encodeQueryParam(include, StandardCharsets.UTF_8)
                + "&orderByDesc=StartDate"
                + "&take=" + take
                + "&format=json"
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
    }
}