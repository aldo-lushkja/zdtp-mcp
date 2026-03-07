package com.ibm.mcp.targetprocess.epic.converter;

import com.ibm.mcp.targetprocess.epic.dto.EpicDto;
import com.ibm.mcp.targetprocess.epic.model.Epic;
import com.ibm.mcp.targetprocess.shared.model.EntityState;
import com.ibm.mcp.targetprocess.shared.model.Owner;
import com.ibm.mcp.targetprocess.shared.model.Project;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class EpicConverter {

    private static final Pattern TP_DATE = Pattern.compile("/Date\\((\\d+)[+-]\\d{4}\\)/");

    public EpicDto toDto(Epic epic) {
        return EpicDto.builder()
                .id(epic.id()).name(epic.name()).description(epic.description())
                .projectName(extractProjectName(epic)).state(extractState(epic))
                .ownerLogin(extractOwnerLogin(epic))
                .effort(epic.effort())
                .createdAt(parseDate(epic.createDate()))
                .endDate(parseDate(epic.endDate()))
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

    private String extractProjectName(Epic epic) {
        return Optional.ofNullable(epic.project()).map(Project::name).orElse(null);
    }

    private String extractState(Epic epic) {
        return Optional.ofNullable(epic.state()).map(EntityState::name).orElse(null);
    }

    private String extractOwnerLogin(Epic epic) {
        return Optional.ofNullable(epic.owner()).map(Owner::login).orElse(null);
    }
}