package com.ibm.mcp.zdtp.request.control;

import com.ibm.mcp.zdtp.request.entity.RequestDto;
import com.ibm.mcp.zdtp.request.entity.Request;
import com.ibm.mcp.zdtp.shared.entity.EntityState;
import com.ibm.mcp.zdtp.shared.entity.Owner;
import com.ibm.mcp.zdtp.shared.entity.Project;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Pattern;
public class RequestConverter {

    private static final Pattern TP_DATE = Pattern.compile("/Date\\((\\d+)[+-]\\d{4}\\)/");

    public RequestDto toDto(Request request) {
        return new RequestDto(
                request.id(),
                request.name(),
                request.description(),
                extractProjectName(request),
                extractState(request),
                extractOwnerLogin(request),
                request.effort(),
                parseDate(request.createDate()),
                parseDate(request.endDate())
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

    private String extractProjectName(Request request) {
        return Optional.ofNullable(request.project()).map(Project::name).orElse(null);
    }

    private String extractState(Request request) {
        return Optional.ofNullable(request.state()).map(EntityState::name).orElse(null);
    }

    private String extractOwnerLogin(Request request) {
        return Optional.ofNullable(request.owner()).map(Owner::login).orElse(null);
    }
}

