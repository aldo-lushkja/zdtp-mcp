package com.ibm.mcp.zdtp.teamiteration.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.teamiteration.entity.TeamIterationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamIterationSearchServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    private static final String SPRINTS_RESPONSE = """
            {"Items":[{"Id":1,"Name":"Sprint 1"},{"Id":2,"Name":"Sprint 2"}]}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    TeamIterationSearchService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        service = new TeamIterationSearchService(props, httpClient, new TeamIterationConverter(), new ObjectMapper());
    }

    @Test
    void search_returnsMappedSprints() {
        givenApiReturns(SPRINTS_RESPONSE);
        List<TeamIterationDto> result = service.searchTeamIterations(null, null, null, null, null, 10);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Sprint 1");
    }

    @Test
    void teamIdFilter_addsTeamIdEqCondition() {
        givenApiReturns(SPRINTS_RESPONSE);
        service.searchTeamIterations(null, 42, null, null, null, 10);
        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8)).contains("Team.Id eq 42");
    }

    @Test
    void startDateFilter_usesGteOnStartDate() {
        givenApiReturns(SPRINTS_RESPONSE);
        service.searchTeamIterations(null, null, null, "2024-01-01", null, 10);
        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8)).contains("StartDate gte '2024-01-01'");
    }

    @Test
    void allFilters_combinedWithAnd() {
        givenApiReturns(SPRINTS_RESPONSE);
        service.searchTeamIterations("Sprint", 42, "Team A", "2024-01-01", null, 10);
        String decodedUrl = URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8);
        assertThat(decodedUrl).contains("Name contains 'Sprint'");
        assertThat(decodedUrl).contains("Team.Id eq 42");
        assertThat(decodedUrl).contains("Team.Name contains 'Team A'");
        assertThat(decodedUrl).contains("StartDate gte '2024-01-01'");
    }

    private void givenApiReturns(String body) {
        when(httpClient.fetch(any())).thenReturn(body);
        when(httpClient.parse(any(), any())).thenCallRealMethod();
    }

    private String captureUrl() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(httpClient).fetch(captor.capture());
        return captor.getValue();
    }
}
