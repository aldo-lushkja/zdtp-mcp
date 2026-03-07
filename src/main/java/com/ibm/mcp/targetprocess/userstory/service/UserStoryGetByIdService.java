package com.ibm.mcp.targetprocess.userstory.service;

import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.userstory.converter.UserStoryConverter;
import com.ibm.mcp.targetprocess.userstory.dto.UserStoryDto;
import com.ibm.mcp.targetprocess.userstory.model.UserStory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Service
public class UserStoryGetByIdService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login],AssignedUser[Id,Login]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final UserStoryConverter converter;

    public UserStoryGetByIdService(TargetProcessProperties properties,
                                   TargetProcessHttpClient httpClient,
                                   UserStoryConverter converter) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
    }

    public UserStoryDto getById(int id) {
        String url = buildUrl(id);
        String response = httpClient.fetch(url);
        UserStory story = httpClient.parseSingle(response, UserStory.class);
        return converter.toDto(story);
    }

    private String buildUrl(int id) {
        return properties.baseUrl() + "/api/v1/UserStories/" + id
                + "?format=json"
                + "&include=" + UriUtils.encodeQueryParam(INCLUDE, StandardCharsets.UTF_8)
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
    }
}
