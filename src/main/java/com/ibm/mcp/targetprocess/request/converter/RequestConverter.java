package com.ibm.mcp.targetprocess.request.converter;

import com.ibm.mcp.targetprocess.request.dto.RequestDto;
import com.ibm.mcp.targetprocess.request.model.Request;
import com.ibm.mcp.targetprocess.shared.model.EntityState;
import com.ibm.mcp.targetprocess.shared.model.Owner;
import com.ibm.mcp.targetprocess.shared.model.Project;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class RequestConverter {

    private static final Pattern TP_DATE = Pattern.compile("/Date\\((\\d+)[+-]\\d{4}\\)/");

    public RequestDto toDto(Request request) {
        return RequestDto.builder()
                .id(request.id()).name(request.name()).description(request.description())
                .projectName(extractProjectName(request)).state(extractState(request))
                .ownerLogin(extractOwnerLogin(request))
                .effort(request.effort())
                .createdAt(parseDate(request.createDate()))
                .endDate(parseDate(request.endDate()))
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
