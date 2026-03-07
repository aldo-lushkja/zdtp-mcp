package com.ibm.mcp.zdtp.team.control;

import com.ibm.mcp.zdtp.team.entity.TeamDto;
import com.ibm.mcp.zdtp.team.entity.Team;
public class TeamConverter {

    public TeamDto toDto(Team team) {
        return new TeamDto(
                team.id(),
                team.name()
        );
    }
}