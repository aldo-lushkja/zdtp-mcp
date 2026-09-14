package com.ibm.mcp.zdtp.assignment.control;

import com.ibm.mcp.zdtp.assignment.entity.Assignment;
import com.ibm.mcp.zdtp.assignment.entity.AssignmentDto;

public class AssignmentConverter {
    public static Assignment toAssignment(AssignmentDto dto) {
        if (dto == null) return null;
        return new Assignment(
                dto.id(),
                dto.role() != null ? dto.role().id() : null,
                dto.role() != null ? dto.role().name() : null,
                dto.general() != null ? dto.general().id() : null,
                dto.general() != null ? dto.general().name() : null,
                dto.generalUser() != null ? dto.generalUser().id() : null,
                dto.generalUser() != null ? dto.generalUser().login() : null
        );
    }
}
