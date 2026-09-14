package com.ibm.mcp.zdtp.customfield.control;

import com.ibm.mcp.zdtp.customfield.entity.CustomFieldDto;
import com.ibm.mcp.zdtp.customfield.entity.CustomFieldInfo;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import java.util.List;
import java.util.Map;

public class CustomFieldUpdateService {
    private static final QueryEngine.Domain ASSIGNABLE_DOMAIN = new QueryEngine.Domain("Assignables", "[Id,Name]");
    private final QueryEngine engine;

    public CustomFieldUpdateService(QueryEngine engine) {
        this.engine = engine;
    }

    public CustomFieldInfo updateCustomField(int entityId, String fieldName, Object fieldValue) {
        Map<String, Object> fieldMap = Map.of(
                "Name", fieldName,
                "Value", fieldValue
        );
        Map<String, Object> body = Map.of(
                "CustomFields", List.of(fieldMap)
        );

        engine.update(
                ASSIGNABLE_DOMAIN,
                entityId,
                body,
                dto -> dto,
                CustomFieldDto.class
        );

        return new CustomFieldInfo(null, fieldName, null, null, fieldValue);
    }
}
