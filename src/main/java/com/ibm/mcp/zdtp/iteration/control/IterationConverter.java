package com.ibm.mcp.zdtp.iteration.control;

import com.ibm.mcp.zdtp.iteration.entity.Iteration;
import com.ibm.mcp.zdtp.iteration.entity.IterationDto;

public class IterationConverter {
    public static Iteration toIteration(IterationDto dto) {
        if (dto == null) return null;
        return new Iteration(
                dto.id(),
                dto.name(),
                dto.startDate(),
                dto.endDate(),
                dto.project() != null ? dto.project().id() : null,
                dto.project() != null ? dto.project().name() : null
        );
    }
}
