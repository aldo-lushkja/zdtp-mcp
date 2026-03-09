package com.ibm.mcp.zdtp.release.control;

import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

public class ReleaseDeleteService extends BaseService {
    public ReleaseDeleteService(QueryEngine engine) {
        super(engine);
    }

    public void delete(int id) {
        engine.delete(QueryEngine.RELEASE, id);
    }
}
