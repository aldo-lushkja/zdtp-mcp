package com.ibm.mcp.targetprocess.testplan.converter;

import com.ibm.mcp.targetprocess.shared.model.EntityState;
import com.ibm.mcp.targetprocess.shared.model.Owner;
import com.ibm.mcp.targetprocess.shared.model.Project;
import com.ibm.mcp.targetprocess.testplan.dto.TestPlanDto;
import com.ibm.mcp.targetprocess.testplan.model.TestPlan;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class TestPlanConverter {

    private static final Pattern TP_DATE = Pattern.compile("/Date\\((\\d+)[+-]\\d{4}\\)/");

    public TestPlanDto toDto(TestPlan testPlan) {
        return TestPlanDto.builder()
                .id(testPlan.id()).name(testPlan.name()).description(testPlan.description())
                .projectName(extractProjectName(testPlan)).state(extractState(testPlan))
                .ownerLogin(extractOwnerLogin(testPlan))
                .createdAt(parseDate(testPlan.createDate()))
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

    private String extractProjectName(TestPlan testPlan) {
        return Optional.ofNullable(testPlan.project()).map(Project::name).orElse(null);
    }

    private String extractState(TestPlan testPlan) {
        return Optional.ofNullable(testPlan.state()).map(EntityState::name).orElse(null);
    }

    private String extractOwnerLogin(TestPlan testPlan) {
        return Optional.ofNullable(testPlan.owner()).map(Owner::login).orElse(null);
    }
}