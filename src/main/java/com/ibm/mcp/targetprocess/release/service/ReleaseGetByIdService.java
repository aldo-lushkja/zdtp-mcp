package com.ibm.mcp.targetprocess.release.service;

import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.release.converter.ReleaseConverter;
import com.ibm.mcp.targetprocess.release.dto.ReleaseDto;
import com.ibm.mcp.targetprocess.release.model.Release;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Service
public class ReleaseGetByIdService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,StartDate,EndDate,Effort,Owner[Id,Login]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final ReleaseConverter converter;

    public ReleaseGetByIdService(TargetProcessProperties properties,
                                 TargetProcessHttpClient httpClient,
                                 ReleaseConverter converter) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
    }

    public ReleaseDto getById(int id) {
        String url = buildUrl(id);
        String response = httpClient.fetch(url);
        Release release = httpClient.parseSingle(response, Release.class);
        return converter.toDto(release);
    }

    private String buildUrl(int id) {
        return properties.baseUrl() + "/api/v1/Releases/" + id
                + "?format=json"
                + "&include=" + UriUtils.encodeQueryParam(INCLUDE, StandardCharsets.UTF_8)
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
    }
}
