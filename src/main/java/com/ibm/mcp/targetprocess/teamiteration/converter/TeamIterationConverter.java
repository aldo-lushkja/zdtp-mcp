package com.ibm.mcp.targetprocess.teamiteration.converter;

import com.ibm.mcp.targetprocess.team.model.Team;
import com.ibm.mcp.targetprocess.teamiteration.dto.TeamIterationDto;
import com.ibm.mcp.targetprocess.teamiteration.model.TeamIteration;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class TeamIterationConverter {

    private static final Pattern TP_DATE = Pattern.compile("/Date\\((\\d+)[+-]\\d{4}\\)/");

    public TeamIterationDto toDto(TeamIteration teamIteration) {
        return TeamIterationDto.builder()
                .id(teamIteration.id())
                .name(teamIteration.name())
                .startDate(parseDate(teamIteration.startDate()))
                .endDate(parseDate(teamIteration.endDate()))
                .teamId(Optional.ofNullable(teamIteration.team()).map(Team::id).orElse(null))
                .teamName(Optional.ofNullable(teamIteration.team()).map(Team::name).orElse(null))
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