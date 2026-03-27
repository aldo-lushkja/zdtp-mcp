package com.ibm.mcp.zdtp.userstory.control;

import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.userstory.entity.UserStory;
import com.ibm.mcp.zdtp.userstory.entity.UserStoryDto;

public class UserStoryGetByIdService extends BaseService {
    private final UserStoryConverter converter;

    public UserStoryGetByIdService(QueryEngine engine, UserStoryConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public UserStoryDto getById(int id) {
        return get(id);
    }

    public UserStoryDto get(int id) {
        return engine.get(QueryEngine.USER_STORY, id, converter::toDto, UserStory.class);
    }
}
