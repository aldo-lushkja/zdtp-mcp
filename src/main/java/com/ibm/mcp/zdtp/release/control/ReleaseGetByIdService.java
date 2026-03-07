package com.ibm.mcp.zdtp.release.control;

import java.net.URLEncoder;

import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.release.control.ReleaseConverter;
import com.ibm.mcp.zdtp.release.entity.ReleaseDto;
import com.ibm.mcp.zdtp.release.entity.Release;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

import java.nio.charset.StandardCharsets;
public class ReleaseGetByIdService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],CreateDate,StartDate,EndDate,Effort,Owner[Id,Login]]";

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
                + "&include=" + URLEncoder.encode(INCLUDE, StandardCharsets.UTF_8).replace("+", "%20")
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
    }
}
