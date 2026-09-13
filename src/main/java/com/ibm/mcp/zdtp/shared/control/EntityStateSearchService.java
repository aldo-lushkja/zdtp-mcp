package com.ibm.mcp.zdtp.shared.control;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.shared.entity.EntityState;
import com.ibm.mcp.zdtp.shared.model.TargetProcessResponse;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EntityStateSearchService extends BaseService {
    public EntityStateSearchService(QueryEngine engine) {
        super(engine);
    }

    public List<EntityState> listStates() {
        Map<String, String> params = new TreeMap<>();
        params.put("take", "50");
        params.put("orderBy", "Name");
        return engine.list(QueryEngine.ENTITY_STATE, params, new TypeReference<TargetProcessResponse<EntityState>>() {}, state -> state);
    }
}
