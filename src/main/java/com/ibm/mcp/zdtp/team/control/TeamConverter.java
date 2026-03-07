package com.ibm.mcp.zdtp.team.control;

import com.ibm.mcp.zdtp.team.entity.TeamDto;
import com.ibm.mcp.zdtp.team.entity.Team;
public class TeamConverter {

    public TeamDto toDto(Team team) {
        return TeamDto.builder()
                .id(team.id())
                .name(team.name())
                .build();
    }
}