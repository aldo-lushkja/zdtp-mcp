package com.ibm.mcp.zdtp.customfield.control;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.customfield.entity.CustomFieldDto;
import com.ibm.mcp.zdtp.customfield.entity.CustomFieldInfo;
import com.ibm.mcp.zdtp.shared.model.TargetProcessResponse;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomFieldListService {
    private final QueryEngine engine;

    public CustomFieldListService(QueryEngine engine) {
        this.engine = engine;
    }

    public List<CustomFieldInfo> list(String entityTypeName, Integer processId, Integer take) {
        List<String> conditions = new ArrayList<>();
        if (entityTypeName != null && !entityTypeName.isBlank()) {
            conditions.add("EntityKind eq '" + entityTypeName.replace("'", "''") + "'");
        }
        if (processId != null) {
            conditions.add("Process.Id eq " + processId);
        }

        Map<String, String> params = new HashMap<>();
        if (!conditions.isEmpty()) {
            params.put("where", String.join(" and ", conditions));
        }
        if (take != null && take > 0) {
            params.put("take", String.valueOf(take));
        }

        return engine.list(
                QueryEngine.CUSTOM_FIELD,
                params,
                new TypeReference<TargetProcessResponse<CustomFieldDto>>() {},
                CustomFieldConverter::toInfo
        );
    }
}
