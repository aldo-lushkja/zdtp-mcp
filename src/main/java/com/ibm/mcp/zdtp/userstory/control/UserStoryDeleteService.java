package com.ibm.mcp.zdtp.userstory.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

public class UserStoryDeleteService extends BaseService {
    public UserStoryDeleteService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, ObjectMapper mapper) {
        super(properties, httpClient, mapper);
    }

    public void delete(int id) {
        engine.delete(QueryEngine.USER_STORY, id);
    }
}
