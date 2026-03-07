package com.ibm.mcp.zdtp.teamiteration.control;

import java.net.URLEncoder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.entity.TargetProcessResponse;
import com.ibm.mcp.zdtp.teamiteration.control.TeamIterationConverter;
import com.ibm.mcp.zdtp.teamiteration.entity.TeamIterationDto;
import com.ibm.mcp.zdtp.teamiteration.entity.TeamIteration;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
                + "?where=" + URLEncoder.encode(where, StandardCharsets.UTF_8).replace("+", "%20")
                + "&include=" + URLEncoder.encode(include, StandardCharsets.UTF_8).replace("+", "%20")
                + "&orderByDesc=StartDate"
                + "&take=" + take
                + "&format=json"
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
    }
}