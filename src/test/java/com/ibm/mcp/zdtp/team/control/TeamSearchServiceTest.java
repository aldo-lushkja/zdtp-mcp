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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamSearchServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    private static final String TEAMS_RESPONSE = """
            {"Items":[{"Id":1,"Name":"Team 1"},{"Id":2,"Name":"Team 2"}]}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    TeamSearchService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new TeamSearchService(engine, new TeamConverter());
    }

    @Test
    void search_returnsMappedTeams() {
        givenApiReturns(TEAMS_RESPONSE);
        var result = service.searchTeams(null, 10);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Team 1");
    }

    private void givenApiReturns(String body) {
        when(httpClient.fetch(any())).thenReturn(body);
        when(httpClient.parse(any(), any())).thenCallRealMethod();
    }
}






