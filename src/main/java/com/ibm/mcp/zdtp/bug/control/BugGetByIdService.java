package com.ibm.mcp.zdtp.bug.control;

import com.ibm.mcp.zdtp.bug.entity.Bug;
import com.ibm.mcp.zdtp.bug.entity.BugDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

public class BugGetByIdService extends BaseService {
    private final BugConverter converter;

    public BugGetByIdService(QueryEngine engine, BugConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public BugDto get(int id) {
        return engine.get(QueryEngine.BUG, id, converter::toDto, Bug.class);
    }
}
