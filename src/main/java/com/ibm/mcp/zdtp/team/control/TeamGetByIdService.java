package com.ibm.mcp.zdtp.team.control;

import java.net.URLEncoder;

import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.team.control.TeamConverter;
import com.ibm.mcp.zdtp.team.entity.TeamDto;
import com.ibm.mcp.zdtp.team.entity.Team;

import java.nio.charset.StandardCharsets;
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
                + "&include=" + URLEncoder.encode(INCLUDE, StandardCharsets.UTF_8).replace("+", "%20")
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
    }
}