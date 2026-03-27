package com.ibm.mcp.zdtp.team.control;

import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.team.entity.TeamDto;
import com.ibm.mcp.zdtp.team.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamGetByIdServiceTest {

    @Mock
    TargetProcessHttpClient httpClient;

    TeamGetByIdService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties("http://test", "token");
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new TeamGetByIdService(engine, new TeamConverter());
    }

    @Test
    void getById_returnsMappedTeam() {
        String body = "{\"Id\":123,\"Name\":\"Team A\"}";
        when(httpClient.fetch(any())).thenReturn(body);
        when(httpClient.parseSingle(eq(body), eq(Team.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(body, Team.class));

        TeamDto result = service.getById(123);
        assertThat(result.id()).isEqualTo(123);
        assertThat(result.name()).isEqualTo("Team A");
    }
}






