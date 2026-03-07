package com.ibm.mcp.zdtp.epic.control;

import java.net.URLEncoder;

import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.epic.control.EpicConverter;
import com.ibm.mcp.zdtp.epic.entity.EpicDto;
import com.ibm.mcp.zdtp.epic.entity.Epic;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

import java.nio.charset.StandardCharsets;
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
                + "&include=" + URLEncoder.encode(INCLUDE, StandardCharsets.UTF_8).replace("+", "%20")
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
    }
}