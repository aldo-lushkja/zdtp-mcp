package com.ibm.mcp.zdtp.userstory.control;

import java.util.Map;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.userstory.entity.UserStory;
import com.ibm.mcp.zdtp.userstory.entity.UserStoryDto;

public class UserStoryGetByIdService extends BaseService {
    private static final String INCLUDE = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login],AssignedUser[Id,Login],Release[Id,Name],TeamIteration[Id,Name]]";
    private final UserStoryConverter converter;

    public UserStoryGetByIdService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, UserStoryConverter converter) {
        super(properties, httpClient);
        this.converter = converter;
    }

    public UserStoryDto getById(int id) {
        return get(id);
    }

    public UserStoryDto get(int id) {
        Map<String, String> parameters = Map.of("include", INCLUDE);
        return fetchSingle("UserStories/" + id, parameters, UserStory.class, converter::toDto);
    }
}
