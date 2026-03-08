package com.ibm.mcp.zdtp.testplan.control;

import com.ibm.mcp.zdtp.shared.entity.EntityState;
import com.ibm.mcp.zdtp.shared.entity.Owner;
import com.ibm.mcp.zdtp.shared.entity.Project;
import com.ibm.mcp.zdtp.testplan.entity.TestPlanDto;
import com.ibm.mcp.zdtp.testplan.entity.TestPlan;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Pattern;
public class TestPlanConverter {

    private static final Pattern TP_DATE = Pattern.compile("/Date\\((\\d+)[+-]\\d{4}\\)/");

    public TestPlanDto toDto(TestPlan testPlan) {
        return new TestPlanDto(
                testPlan.id(),
                testPlan.name(),
                testPlan.description(),
                extractProjectName(testPlan),
                extractState(testPlan),
                extractStateId(testPlan),
                extractOwnerLogin(testPlan),
                parseDate(testPlan.createDate())
        );
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

    private String extractProjectName(TestPlan testPlan) {
        return Optional.ofNullable(testPlan.project()).map(Project::name).orElse(null);
    }

    private String extractState(TestPlan testPlan) {
        return Optional.ofNullable(testPlan.state()).map(EntityState::name).orElse(null);
    }

    private Integer extractStateId(TestPlan testPlan) {
        return Optional.ofNullable(testPlan.state()).map(EntityState::id).orElse(null);
    }

    private String extractOwnerLogin(TestPlan testPlan) {
        return Optional.ofNullable(testPlan.owner()).map(Owner::login).orElse(null);
    }
}

