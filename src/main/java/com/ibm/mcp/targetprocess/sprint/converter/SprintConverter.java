package com.ibm.mcp.targetprocess.sprint.converter;

import com.ibm.mcp.targetprocess.shared.model.EntityState;
import com.ibm.mcp.targetprocess.shared.model.Owner;
import com.ibm.mcp.targetprocess.shared.model.Project;
import com.ibm.mcp.targetprocess.sprint.dto.SprintDto;
import com.ibm.mcp.targetprocess.sprint.model.Sprint;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class SprintConverter {

    private static final Pattern TP_DATE = Pattern.compile("/Date\\((\\d+)[+-]\\d{4}\\)/");

    public SprintDto toDto(Sprint sprint) {
        return SprintDto.builder()
                .id(sprint.id()).name(sprint.name()).description(sprint.description())
                .projectName(Optional.ofNullable(sprint.project()).map(Project::name).orElse(null))
                .state(Optional.ofNullable(sprint.state()).map(EntityState::name).orElse(null))
                .ownerLogin(Optional.ofNullable(sprint.owner()).map(Owner::login).orElse(null))
                .createdAt(parseDate(sprint.createDate()))
                .startDate(parseDate(sprint.startDate()))
                .endDate(parseDate(sprint.endDate()))
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
}