package com.ibm.mcp.targetprocess.team.service;

import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.team.converter.TeamConverter;
import com.ibm.mcp.targetprocess.team.dto.TeamDto;
import com.ibm.mcp.targetprocess.team.model.Team;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Service
public class TeamGetByIdService {

    private static final String INCLUDE = "[Id,Name]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final TeamConverter converter;

    public TeamGetByIdService(TargetProcessProperties properties,
                              TargetProcessHttpClient httpClient,
                              TeamConverter converter) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
    }

    public TeamDto getById(int id) {
        String url = buildUrl(id);
        String response = httpClient.fetch(url);
        Team team = httpClient.parseSingle(response, Team.class);
        return converter.toDto(team);
    }

    private String buildUrl(int id) {
        return properties.baseUrl() + "/api/v1/Teams/" + id
                + "?format=json"
                + "&include=" + UriUtils.encodeQueryParam(INCLUDE, StandardCharsets.UTF_8)
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
    }
}