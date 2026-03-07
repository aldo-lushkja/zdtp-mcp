package com.ibm.mcp.targetprocess.feature.service;

import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.feature.converter.FeatureConverter;
import com.ibm.mcp.targetprocess.feature.dto.FeatureDto;
import com.ibm.mcp.targetprocess.feature.model.Feature;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Service
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
                + "&include=" + UriUtils.encodeQueryParam(INCLUDE, StandardCharsets.UTF_8)
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
    }
}
