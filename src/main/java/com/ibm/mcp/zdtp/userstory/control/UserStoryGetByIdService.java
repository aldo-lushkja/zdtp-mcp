package com.ibm.mcp.zdtp.userstory.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.userstory.entity.UserStory;
import com.ibm.mcp.zdtp.userstory.entity.UserStoryDto;

public class UserStoryGetByIdService extends BaseService {
    private final UserStoryConverter converter;

    public UserStoryGetByIdService(TargetProcessProperties props, TargetProcessHttpClient http, UserStoryConverter conv, ObjectMapper mapper) {
        super(props, http, mapper); this.converter = conv;
    }

    public UserStoryDto getById(int id) {
        return get(id);
    }

    public UserStoryDto get(int id) {
        return engine.get(QueryEngine.USER_STORY, id, converter::toDto, UserStory.class);
    }
}
