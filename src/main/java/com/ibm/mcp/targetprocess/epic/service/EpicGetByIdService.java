package com.ibm.mcp.targetprocess.epic.service;

import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.epic.converter.EpicConverter;
import com.ibm.mcp.targetprocess.epic.dto.EpicDto;
import com.ibm.mcp.targetprocess.epic.model.Epic;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Service
public class EpicGetByIdService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final EpicConverter converter;

    public EpicGetByIdService(TargetProcessProperties properties,
                              TargetProcessHttpClient httpClient,
                              EpicConverter converter) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
    }

    public EpicDto getById(int id) {
        String url = buildUrl(id);
        String response = httpClient.fetch(url);
        Epic epic = httpClient.parseSingle(response, Epic.class);
        return converter.toDto(epic);
    }

    private String buildUrl(int id) {
        return properties.baseUrl() + "/api/v1/Epics/" + id
                + "?format=json"
                + "&include=" + UriUtils.encodeQueryParam(INCLUDE, StandardCharsets.UTF_8)
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
    }
}