package com.ibm.mcp.zdtp.request.control;

import java.net.URLEncoder;

import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.request.control.RequestConverter;
import com.ibm.mcp.zdtp.request.entity.RequestDto;
import com.ibm.mcp.zdtp.request.entity.Request;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

import java.nio.charset.StandardCharsets;
public class RequestGetByIdService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final RequestConverter converter;

    public RequestGetByIdService(TargetProcessProperties properties,
                                 TargetProcessHttpClient httpClient,
                                 RequestConverter converter) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
    }

    public RequestDto getById(int id) {
        String url = buildUrl(id);
        String response = httpClient.fetch(url);
        Request request = httpClient.parseSingle(response, Request.class);
        return converter.toDto(request);
    }

    private String buildUrl(int id) {
        return properties.baseUrl() + "/api/v1/Requests/" + id
                + "?format=json"
                + "&include=" + URLEncoder.encode(INCLUDE, StandardCharsets.UTF_8).replace("+", "%20")
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
    }
}
