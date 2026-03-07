package com.ibm.mcp.zdtp.teamiteration.control;

import java.net.URLEncoder;

import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.teamiteration.control.TeamIterationConverter;
import com.ibm.mcp.zdtp.teamiteration.entity.TeamIterationDto;
import com.ibm.mcp.zdtp.teamiteration.entity.TeamIteration;

import java.nio.charset.StandardCharsets;
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
                + "&include=" + URLEncoder.encode(INCLUDE, StandardCharsets.UTF_8).replace("+", "%20")
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
    }
}