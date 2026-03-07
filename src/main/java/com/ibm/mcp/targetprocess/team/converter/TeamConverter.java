package com.ibm.mcp.targetprocess.team.converter;

import com.ibm.mcp.targetprocess.team.dto.TeamDto;
import com.ibm.mcp.targetprocess.team.model.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamConverter {

    public TeamDto toDto(Team team) {
        return TeamDto.builder()
                .id(team.id())
                .name(team.name())
                .build();
    }
}