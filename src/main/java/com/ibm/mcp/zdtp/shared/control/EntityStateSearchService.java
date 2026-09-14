package com.ibm.mcp.zdtp.shared.control;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.shared.entity.EntityState;
import com.ibm.mcp.zdtp.shared.model.TargetProcessResponse;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.ibm.mcp.zdtp.shared.cache.TtlCache;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EntityStateSearchService extends BaseService {
    private static final TtlCache<String, List<EntityState>> CACHE = new TtlCache<>(5 * 60 * 1000L);

    public EntityStateSearchService(QueryEngine engine) {
        super(engine);
    }

    public List<EntityState> listStates() {
        List<EntityState> cached = CACHE.get("states");
        if (cached != null) {
            return cached;
        }
        Map<String, String> params = new TreeMap<>();
        params.put("take", "50");
        params.put("orderBy", "Name");
        List<EntityState> states = engine.list(QueryEngine.ENTITY_STATE, params, new TypeReference<TargetProcessResponse<EntityState>>() {}, state -> state);
        CACHE.put("states", states);
        return states;
    }
}
