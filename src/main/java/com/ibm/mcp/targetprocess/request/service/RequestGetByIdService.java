package com.ibm.mcp.targetprocess.request.service;

import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.request.converter.RequestConverter;
import com.ibm.mcp.targetprocess.request.dto.RequestDto;
import com.ibm.mcp.targetprocess.request.model.Request;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Service
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
                + "&include=" + UriUtils.encodeQueryParam(INCLUDE, StandardCharsets.UTF_8)
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
    }
}
