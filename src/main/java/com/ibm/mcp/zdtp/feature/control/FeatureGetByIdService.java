package com.ibm.mcp.zdtp.feature.control;

import java.net.URLEncoder;

import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.feature.control.FeatureConverter;
import com.ibm.mcp.zdtp.feature.entity.FeatureDto;
import com.ibm.mcp.zdtp.feature.entity.Feature;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

import java.nio.charset.StandardCharsets;
public class FeatureGetByIdService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final FeatureConverter converter;

    public FeatureGetByIdService(TargetProcessProperties properties,
                                 TargetProcessHttpClient httpClient,
                                 FeatureConverter converter) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
    }

    public FeatureDto getById(int id) {
        String url = buildUrl(id);
        String response = httpClient.fetch(url);
        Feature feature = httpClient.parseSingle(response, Feature.class);
        return converter.toDto(feature);
    }

    private String buildUrl(int id) {
        return properties.baseUrl() + "/api/v1/Features/" + id
                + "?format=json"
                + "&include=" + URLEncoder.encode(INCLUDE, StandardCharsets.UTF_8).replace("+", "%20")
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
    }
}
