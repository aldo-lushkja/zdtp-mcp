package com.ibm.mcp.zdtp.release.control;

import com.ibm.mcp.zdtp.release.entity.ReleaseDto;
import com.ibm.mcp.zdtp.release.entity.Release;
import com.ibm.mcp.zdtp.shared.entity.EntityState;
import com.ibm.mcp.zdtp.shared.entity.Owner;
import com.ibm.mcp.zdtp.shared.entity.Project;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Pattern;
public class ReleaseConverter {

    private static final Pattern TP_DATE = Pattern.compile("/Date\\((\\d+)[+-]\\d{4}\\)/");

    public ReleaseDto toDto(Release release) {
        return ReleaseDto.builder()
                .id(release.id()).name(release.name()).description(release.description())
                .projectName(extractProjectName(release)).state(extractState(release))
                .ownerLogin(extractOwnerLogin(release))
                .effort(release.effort())
                .createdAt(parseDate(release.createDate()))
                .startDate(parseDate(release.startDate()))
                .endDate(parseDate(release.endDate()))
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

    private String extractProjectName(Release release) {
        return Optional.ofNullable(release.project()).map(Project::name).orElse(null);
    }

    private String extractState(Release release) {
        return Optional.ofNullable(release.state()).map(EntityState::name).orElse(null);
    }

    private String extractOwnerLogin(Release release) {
        return Optional.ofNullable(release.owner()).map(Owner::login).orElse(null);
    }
}
