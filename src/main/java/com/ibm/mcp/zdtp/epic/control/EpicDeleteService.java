package com.ibm.mcp.zdtp.epic.control;

import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

public class EpicDeleteService extends BaseService {
    public EpicDeleteService(QueryEngine engine) {
        super(engine);
    }

    public void delete(int id) {
        engine.delete(QueryEngine.EPIC, id);
    }
}
