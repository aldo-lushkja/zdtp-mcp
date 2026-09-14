package com.ibm.mcp.zdtp.testrun.control;

import com.ibm.mcp.zdtp.testrun.entity.TestCaseRun;
import com.ibm.mcp.zdtp.testrun.entity.TestCaseRunDto;
import com.ibm.mcp.zdtp.testrun.entity.TestRun;
import com.ibm.mcp.zdtp.testrun.entity.TestRunDto;

public class TestRunConverter {
    public static TestRun toTestRun(TestRunDto dto) {
        if (dto == null) return null;
        return new TestRun(
                dto.id(),
                dto.name(),
                dto.createDate(),
                dto.testPlan() != null ? dto.testPlan().id() : null,
                dto.testPlan() != null ? dto.testPlan().name() : null,
                dto.project() != null ? dto.project().id() : null,
                dto.project() != null ? dto.project().name() : null
        );
    }

    public static TestCaseRun toTestCaseRun(TestCaseRunDto dto) {
        if (dto == null) return null;
        return new TestCaseRun(
                dto.id(),
                dto.status(),
                dto.comment(),
                dto.testCase() != null ? dto.testCase().id() : null,
                dto.testCase() != null ? dto.testCase().name() : null,
                dto.testPlanRun() != null ? dto.testPlanRun().id() : null,
                dto.testPlanRun() != null ? dto.testPlanRun().name() : null
        );
    }
}
