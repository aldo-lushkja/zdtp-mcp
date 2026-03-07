package com.ibm.mcp.targetprocess.project.converter;

import com.ibm.mcp.targetprocess.project.dto.ProjectDto;
import com.ibm.mcp.targetprocess.project.model.ProjectData;
import com.ibm.mcp.targetprocess.shared.model.EntityState;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class ProjectConverter {

    private static final Pattern TP_DATE = Pattern.compile("/Date\\((\\d+)[+-]\\d{4}\\)/");

    public ProjectDto toDto(ProjectData project) {
        return ProjectDto.builder()
                .id(project.id()).name(project.name()).description(project.description())
                .state(extractState(project))
                .createdAt(parseDate(project.createDate()))
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

    private String extractState(ProjectData project) {
        return Optional.ofNullable(project.state()).map(EntityState::name).orElse(null);
    }
}