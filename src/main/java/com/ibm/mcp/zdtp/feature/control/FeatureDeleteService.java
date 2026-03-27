package com.ibm.mcp.zdtp.feature.control;

import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

public class FeatureDeleteService extends BaseService {
    public FeatureDeleteService(QueryEngine engine) {
        super(engine);
    }

    public void delete(int id) {
        engine.delete(QueryEngine.FEATURE, id);
    }
}
