package com.ibm.mcp.zdtp.userstory.control;

import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

public class UserStoryDeleteService extends BaseService {
    public UserStoryDeleteService(QueryEngine engine) {
        super(engine);
    }

    public void delete(int id) {
        engine.delete(QueryEngine.USER_STORY, id);
    }
}
