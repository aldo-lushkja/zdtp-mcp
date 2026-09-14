package com.ibm.mcp.zdtp.customfield.control;

import com.ibm.mcp.zdtp.customfield.entity.CustomFieldDto;
import com.ibm.mcp.zdtp.customfield.entity.CustomFieldInfo;

public class CustomFieldConverter {
    public static CustomFieldInfo toInfo(CustomFieldDto dto) {
        if (dto == null) return null;
        return new CustomFieldInfo(
                dto.id(),
                dto.name(),
                dto.fieldType(),
                dto.entityKind(),
                dto.value()
        );
    }
}
