package com.ibm.mcp.zdtp.project.control;

import com.ibm.mcp.zdtp.project.entity.ProjectDto;
import com.ibm.mcp.zdtp.project.entity.ProjectData;
import com.ibm.mcp.zdtp.shared.entity.EntityState;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Pattern;
public class ProjectConverter {

    private static final Pattern TP_DATE = Pattern.compile("/Date\\((\\d+)[+-]\\d{4}\\)/");

    public ProjectDto toDto(ProjectData project) {
        return new ProjectDto(
                project.id(),
                project.name(),
                project.description(),
                extractState(project),
                parseDate(project.createDate())
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

    private String extractState(ProjectData project) {
        return Optional.ofNullable(project.state()).map(EntityState::name).orElse(null);
    }
}