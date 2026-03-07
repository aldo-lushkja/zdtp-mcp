package com.ibm.mcp.zdtp.feature.control;

import com.ibm.mcp.zdtp.feature.entity.FeatureDto;
import com.ibm.mcp.zdtp.feature.entity.Feature;
import com.ibm.mcp.zdtp.shared.entity.EntityState;
import com.ibm.mcp.zdtp.shared.entity.Owner;
import com.ibm.mcp.zdtp.shared.entity.Project;
import com.ibm.mcp.zdtp.shared.entity.SprintReference;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Pattern;
public class FeatureConverter {

    private static final Pattern TP_DATE = Pattern.compile("/Date\\((\\d+)[+-]\\d{4}\\)/");

    public FeatureDto toDto(Feature feature) {
        return new FeatureDto(
                feature.id(),
                feature.name(),
                feature.description(),
                extractProjectName(feature),
                extractState(feature),
                extractOwnerLogin(feature),
                feature.effort(),
                parseDate(feature.createDate()),
                parseDate(feature.endDate()),
                Optional.ofNullable(feature.sprint()).map(SprintReference::id).orElse(null),
                Optional.ofNullable(feature.sprint()).map(SprintReference::name).orElse(null)
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
