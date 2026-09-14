package com.ibm.mcp.zdtp.testrun.control;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.shared.model.TargetProcessResponse;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import com.ibm.mcp.zdtp.testrun.entity.TestRun;
import com.ibm.mcp.zdtp.testrun.entity.TestRunDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestRunSearchService {
    private final QueryEngine engine;

    public TestRunSearchService(QueryEngine engine) {
        this.engine = engine;
    }

    public List<TestRun> search(String nameQuery, Integer projectId, Integer testPlanId, Integer take) {
        List<String> conditions = new ArrayList<>();
        if (nameQuery != null && !nameQuery.isBlank()) {
            conditions.add("Name contains '" + nameQuery.replace("'", "''") + "'");
        }
        if (projectId != null) {
            conditions.add("Project.Id eq " + projectId);
        }
        if (testPlanId != null) {
            conditions.add("TestPlan.Id eq " + testPlanId);
        }

        Map<String, String> params = new HashMap<>();
        if (!conditions.isEmpty()) {
            params.put("where", String.join(" and ", conditions));
        }
        if (take != null && take > 0) {
            params.put("take", String.valueOf(take));
        }

        return engine.list(
                QueryEngine.TEST_RUN,
                params,
                new TypeReference<TargetProcessResponse<TestRunDto>>() {},
                TestRunConverter::toTestRun
        );
    }
}
