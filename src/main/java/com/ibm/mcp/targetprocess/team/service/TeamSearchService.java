package com.ibm.mcp.targetprocess.team.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.shared.model.TargetProcessResponse;
import com.ibm.mcp.targetprocess.team.converter.TeamConverter;
import com.ibm.mcp.targetprocess.team.dto.TeamDto;
import com.ibm.mcp.targetprocess.team.model.Team;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
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
                + "?where=" + UriUtils.encodeQueryParam(where, StandardCharsets.UTF_8)
                + "&include=" + UriUtils.encodeQueryParam(include, StandardCharsets.UTF_8)
                + "&orderByDesc=Name"
                + "&take=" + take
                + "&format=json"
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
    }
}