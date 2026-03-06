package com.ibm.mcp.targetprocess.service;

import com.ibm.mcp.targetprocess.config.TargetprocessProperties;
import com.ibm.mcp.targetprocess.model.TargetprocessResponse;
import com.ibm.mcp.targetprocess.model.UserStory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class TargetprocessService {

    private final RestClient restClient;
    private final TargetprocessProperties properties;

    public TargetprocessService(RestClient restClient, TargetprocessProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public List<UserStory> searchUserStories(String query) {
        String where = String.format("(Name contains '%s')", query);
        String select = "{id,name,description,project:{id,name},entityState:{id,name}}";

        TargetprocessResponse<UserStory> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v2/UserStory")
                        .queryParam("where", where)
                        .queryParam("select", select)
                        .queryParam("access_token", properties.accessToken())
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<TargetprocessResponse<UserStory>>() {});

        return response != null ? response.items() : List.of();
    }
}
