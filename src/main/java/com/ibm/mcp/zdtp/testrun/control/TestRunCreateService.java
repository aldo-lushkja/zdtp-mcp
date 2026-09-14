package com.ibm.mcp.zdtp.testrun.control;

import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.testrun.entity.TestRun;
import com.ibm.mcp.zdtp.testrun.entity.TestRunDto;

import java.util.HashMap;
import java.util.Map;

public class TestRunCreateService {
    private final QueryEngine engine;

    public TestRunCreateService(QueryEngine engine) {
        this.engine = engine;
    }

    public TestRun create(String name, int projectId, int testPlanId, String description) {
        Map<String, Object> body = new HashMap<>();
        body.put("Name", name);
        body.put("Project", Map.of("Id", projectId));
        body.put("TestPlan", Map.of("Id", testPlanId));
        if (description != null && !description.isBlank()) {
            body.put("Description", description);
        }

        return engine.create(
                QueryEngine.TEST_RUN,
                body,
                TestRunConverter::toTestRun,
                TestRunDto.class
        );
    }
}
