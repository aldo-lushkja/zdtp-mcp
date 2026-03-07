package com.ibm.mcp.targetprocess.teamiteration.service;

import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.teamiteration.converter.TeamIterationConverter;
import com.ibm.mcp.targetprocess.teamiteration.dto.TeamIterationDto;
import com.ibm.mcp.targetprocess.teamiteration.model.TeamIteration;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Service
public class TeamIterationGetByIdService {

    private static final String INCLUDE = "[Id,Name,StartDate,EndDate,Team[Id,Name]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final TeamIterationConverter converter;

    public TeamIterationGetByIdService(TargetProcessProperties properties,
                                       TargetProcessHttpClient httpClient,
                                       TeamIterationConverter converter) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
    }

    public TeamIterationDto getById(int id) {
        String url = buildUrl(id);
        String response = httpClient.fetch(url);
        TeamIteration teamIteration = httpClient.parseSingle(response, TeamIteration.class);
        return converter.toDto(teamIteration);
    }

    private String buildUrl(int id) {
        return properties.baseUrl() + "/api/v1/TeamIterations/" + id
                + "?format=json"
                + "&include=" + UriUtils.encodeQueryParam(INCLUDE, StandardCharsets.UTF_8)
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
    }
}