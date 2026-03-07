package com.ibm.mcp.zdtp.team.control;

import java.net.URLEncoder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.entity.TargetProcessResponse;
import com.ibm.mcp.zdtp.team.control.TeamConverter;
import com.ibm.mcp.zdtp.team.entity.TeamDto;
import com.ibm.mcp.zdtp.team.entity.Team;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public class TeamSearchService {

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final TeamConverter converter;

    public TeamSearchService(TargetProcessProperties properties,
                             TargetProcessHttpClient httpClient,
                             TeamConverter converter) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
    }

    public List<TeamDto> searchTeams(String nameQuery, int take) {
        String url = buildUrl(nameQuery, take);
        String body = httpClient.fetch(url);
        TargetProcessResponse<Team> resp = httpClient.parse(body, new TypeReference<>() {});
        return Optional.ofNullable(resp.items()).orElse(List.of())
                .stream().map(converter::toDto).toList();
    }

    private String buildUrl(String nameQuery, int take) {
        List<String> conditions = new ArrayList<>();
        if (nameQuery != null && !nameQuery.isBlank()) {
            conditions.add("Name contains '%s'".formatted(nameQuery));
        }
        String where = String.join(" and ", conditions);
        String include = "[Id,Name]";
        return properties.baseUrl() + "/api/v1/Teams"
                + "?where=" + URLEncoder.encode(where, StandardCharsets.UTF_8).replace("+", "%20")
                + "&include=" + URLEncoder.encode(include, StandardCharsets.UTF_8).replace("+", "%20")
                + "&orderByDesc=Name"
                + "&take=" + take
                + "&format=json"
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
    }
}