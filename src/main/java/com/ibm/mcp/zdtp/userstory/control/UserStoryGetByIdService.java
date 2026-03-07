package com.ibm.mcp.zdtp.userstory.control;

import java.net.URLEncoder;

import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.userstory.control.UserStoryConverter;
import com.ibm.mcp.zdtp.userstory.entity.UserStoryDto;
import com.ibm.mcp.zdtp.userstory.entity.UserStory;

import java.nio.charset.StandardCharsets;
public class UserStoryGetByIdService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login],AssignedUser[Id,Login],Release[Id,Name]]";

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
                + "&include=" + URLEncoder.encode(INCLUDE, StandardCharsets.UTF_8).replace("+", "%20")
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
    }
}
