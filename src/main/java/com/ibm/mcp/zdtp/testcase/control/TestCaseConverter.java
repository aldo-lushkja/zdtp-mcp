package com.ibm.mcp.zdtp.testcase.control;

import com.ibm.mcp.zdtp.shared.entity.EntityState;
import com.ibm.mcp.zdtp.shared.entity.Owner;
import com.ibm.mcp.zdtp.shared.entity.Project;
import com.ibm.mcp.zdtp.testcase.entity.TestCaseDto;
import com.ibm.mcp.zdtp.testcase.entity.TestCase;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Pattern;
public class TestCaseConverter {

    private static final Pattern TP_DATE = Pattern.compile("/Date\\((\\d+)[+-]\\d{4}\\)/");

    public TestCaseDto toDto(TestCase testCase) {
        return TestCaseDto.builder()
                .id(testCase.id()).name(testCase.name()).description(testCase.description())
                .projectName(extractProjectName(testCase)).state(extractState(testCase))
                .ownerLogin(extractOwnerLogin(testCase))
                .createdAt(parseDate(testCase.createDate()))
                .testPlanName(extractTestPlanName(testCase))
                .build();
    }

    private String parseDate(String raw) {
        if (raw == null) return null;
        var m = TP_DATE.matcher(raw);
        if (m.find()) {
            return Instant.ofEpochMilli(Long.parseLong(m.group(1)))
                    .atZone(ZoneOffset.UTC).toLocalDate().toString();
        }
        return raw;
    }

    private String extractProjectName(TestCase testCase) {
        return Optional.ofNullable(testCase.project()).map(Project::name).orElse(null);
    }

    private String extractState(TestCase testCase) {
        return Optional.ofNullable(testCase.state()).map(EntityState::name).orElse(null);
    }

    private String extractOwnerLogin(TestCase testCase) {
        return Optional.ofNullable(testCase.owner()).map(Owner::login).orElse(null);
    }

    private String extractTestPlanName(TestCase testCase) {
        return Optional.ofNullable(testCase.testPlans())
                .map(TestCase.TestPlanCollection::items)
                .filter(l -> !l.isEmpty())
                .map(l -> l.get(0).name())
                .orElse(null);
    }
}