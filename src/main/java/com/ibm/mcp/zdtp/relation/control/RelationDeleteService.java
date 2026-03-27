package com.ibm.mcp.zdtp.relation.control;

import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

public class RelationDeleteService extends BaseService {
    public RelationDeleteService(QueryEngine engine) {
        super(engine);
    }

    public void delete(int id) {
        engine.delete(QueryEngine.RELATION, id);
    }
}
