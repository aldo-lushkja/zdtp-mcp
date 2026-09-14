package com.ibm.mcp.zdtp.testrun.control;

import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.testrun.entity.TestCaseRun;
import com.ibm.mcp.zdtp.testrun.entity.TestCaseRunDto;

import java.util.HashMap;
import java.util.Map;

public class TestCaseRunUpdateService {
    private final QueryEngine engine;

    public TestCaseRunUpdateService(QueryEngine engine) {
        this.engine = engine;
    }

    public TestCaseRun update(int id, String status, String comment) {
        Map<String, Object> body = new HashMap<>();
        if (status != null && !status.isBlank()) {
            body.put("Status", status);
        }
        if (comment != null) {
            body.put("Comment", comment);
        }

        return engine.update(
                QueryEngine.TEST_CASE_RUN,
                id,
                body,
                TestRunConverter::toTestCaseRun,
                TestCaseRunDto.class
        );
    }
}
