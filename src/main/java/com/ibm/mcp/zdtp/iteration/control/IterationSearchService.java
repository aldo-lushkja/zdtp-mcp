package com.ibm.mcp.zdtp.iteration.control;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.iteration.entity.Iteration;
import com.ibm.mcp.zdtp.iteration.entity.IterationDto;
import com.ibm.mcp.zdtp.shared.model.TargetProcessResponse;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IterationSearchService {
    private final QueryEngine engine;

    public IterationSearchService(QueryEngine engine) {
        this.engine = engine;
    }

    public List<Iteration> search(String nameQuery, Integer projectId, String startDate, String endDate, Integer take) {
        List<String> conditions = new ArrayList<>();
        if (nameQuery != null && !nameQuery.isBlank()) {
            conditions.add("Name contains '" + nameQuery.replace("'", "''") + "'");
        }
        if (projectId != null) {
            conditions.add("Project.Id eq " + projectId);
        }
        if (startDate != null && !startDate.isBlank()) {
            conditions.add("StartDate gte '" + startDate + "'");
        }
        if (endDate != null && !endDate.isBlank()) {
            conditions.add("EndDate lte '" + endDate + "'");
        }

        Map<String, String> params = new HashMap<>();
        if (!conditions.isEmpty()) {
            params.put("where", String.join(" and ", conditions));
        }
        if (take != null && take > 0) {
            params.put("take", String.valueOf(take));
        }

        return engine.list(
                QueryEngine.ITERATION,
                params,
                new TypeReference<TargetProcessResponse<IterationDto>>() {},
                IterationConverter::toIteration
        );
    }
}
