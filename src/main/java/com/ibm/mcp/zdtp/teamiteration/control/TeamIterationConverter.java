package com.ibm.mcp.zdtp.teamiteration.control;

import com.ibm.mcp.zdtp.team.entity.Team;
import com.ibm.mcp.zdtp.teamiteration.entity.TeamIterationDto;
import com.ibm.mcp.zdtp.teamiteration.entity.TeamIteration;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Pattern;
public class TeamIterationConverter {

    private static final Pattern TP_DATE = Pattern.compile("/Date\\((\\d+)[+-]\\d{4}\\)/");

    public TeamIterationDto toDto(TeamIteration teamIteration) {
        return new TeamIterationDto(
                teamIteration.id(),
                teamIteration.name(),
                parseDate(teamIteration.startDate()),
                parseDate(teamIteration.endDate()),
                Optional.ofNullable(teamIteration.team()).map(Team::id).orElse(null),
                Optional.ofNullable(teamIteration.team()).map(Team::name).orElse(null)
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
}