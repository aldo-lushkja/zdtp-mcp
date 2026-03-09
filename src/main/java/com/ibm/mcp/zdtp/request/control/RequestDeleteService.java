package com.ibm.mcp.zdtp.request.control;

import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

public class RequestDeleteService extends BaseService {
    public RequestDeleteService(QueryEngine engine) {
        super(engine);
    }

    public void delete(int id) {
        engine.delete(QueryEngine.REQUEST, id);
    }
}
