package com.ibm.mcp.zdtp.testcase.control;

import com.ibm.mcp.zdtp.testcase.entity.TestStep;
import com.ibm.mcp.zdtp.testcase.entity.TestStepDto;

import java.util.Optional;

public class TestStepConverter {
    public TestStepDto toDto(TestStep step) {
        return new TestStepDto(
                step.id(),
                step.description(),
                step.result(),
                step.runOrder() != null ? step.runOrder() : 0,
                Optional.ofNullable(step.testCase()).map(TestStep.TestCaseRef::id).orElse(0)
        );
    }
}
