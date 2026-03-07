package com.ibm.mcp.targetprocess.feature.converter;

import com.ibm.mcp.targetprocess.feature.dto.FeatureDto;
import com.ibm.mcp.targetprocess.feature.model.Feature;
import com.ibm.mcp.targetprocess.shared.model.EntityState;
import com.ibm.mcp.targetprocess.shared.model.Owner;
import com.ibm.mcp.targetprocess.shared.model.Project;
import com.ibm.mcp.targetprocess.shared.model.SprintReference;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class FeatureConverter {

    private static final Pattern TP_DATE = Pattern.compile("/Date\\((\\d+)[+-]\\d{4}\\)/");

    public FeatureDto toDto(Feature feature) {
        return FeatureDto.builder()
                .id(feature.id()).name(feature.name()).description(feature.description())
                .projectName(extractProjectName(feature)).state(extractState(feature))
                .ownerLogin(extractOwnerLogin(feature))
                .effort(feature.effort())
                .createdAt(parseDate(feature.createDate()))
                .endDate(parseDate(feature.endDate()))
                .sprintId(Optional.ofNullable(feature.sprint()).map(SprintReference::id).orElse(null))
                .sprintName(Optional.ofNullable(feature.sprint()).map(SprintReference::name).orElse(null))
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

    private String extractProjectName(Feature feature) {
        return Optional.ofNullable(feature.project()).map(Project::name).orElse(null);
    }

    private String extractState(Feature feature) {
        return Optional.ofNullable(feature.state()).map(EntityState::name).orElse(null);
    }

    private String extractOwnerLogin(Feature feature) {
        return Optional.ofNullable(feature.owner()).map(Owner::login).orElse(null);
    }
}
