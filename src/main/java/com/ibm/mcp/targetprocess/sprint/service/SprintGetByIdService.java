package com.ibm.mcp.targetprocess.sprint.service;

import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.sprint.converter.SprintConverter;
import com.ibm.mcp.targetprocess.sprint.dto.SprintDto;
import com.ibm.mcp.targetprocess.sprint.model.Sprint;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Service
public class SprintGetByIdService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,StartDate,EndDate,Owner[Id,Login]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final SprintConverter converter;

    public SprintGetByIdService(TargetProcessProperties properties,
                                TargetProcessHttpClient httpClient,
                                SprintConverter converter) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
    }

    public SprintDto getById(int id) {
        String url = buildUrl(id);
        String response = httpClient.fetch(url);
        Sprint sprint = httpClient.parseSingle(response, Sprint.class);
        return converter.toDto(sprint);
    }

    private String buildUrl(int id) {
        return properties.baseUrl() + "/api/v1/Sprints/" + id
                + "?format=json"
                + "&include=" + UriUtils.encodeQueryParam(INCLUDE, StandardCharsets.UTF_8)
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
    }
}