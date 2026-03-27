package com.ibm.mcp.zdtp.teamiteration.control;

import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.teamiteration.entity.TeamIterationDto;
import com.ibm.mcp.zdtp.teamiteration.entity.TeamIteration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamIterationGetByIdServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    private static final String SPRINT_RESPONSE = """
            {"Id":123,"Name":"Sprint 1","StartDate":"\\/Date(1736899200000+0000)\\/",
            "EndDate":"\\/Date(1738108800000+0000)\\/","Team":{"Id":5,"Name":"Team A"}}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    TeamIterationGetByIdService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new TeamIterationGetByIdService(engine, new TeamIterationConverter());
    }

    @Test
    void urlContainsIterationId() {
        givenApiReturns(SPRINT_RESPONSE);
        service.getById(123);
        assertThat(captureUrl()).contains("/api/v1/TeamIterations/123");
    }

    @Test
    void urlContainsRequiredIncludeFields() {
        givenApiReturns(SPRINT_RESPONSE);
        service.getById(123);
        String url = captureUrl();
        assertThat(url).contains("include=");
        assertThat(URLDecoder.decode(url, StandardCharsets.UTF_8)).contains("Team");
    }

    @Test
    void validResponse_mapsFieldsCorrectly() {
        givenApiReturns(SPRINT_RESPONSE);
        TeamIterationDto result = service.getById(123);
        assertThat(result.id()).isEqualTo(123);
        assertThat(result.name()).isEqualTo("Sprint 1");
        assertThat(result.teamName()).isEqualTo("Team A");
    }

    private void givenApiReturns(String body) {
        when(httpClient.fetch(any())).thenReturn(body);
        when(httpClient.parseSingle(eq(body), eq(TeamIteration.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(body, TeamIteration.class));
    }

    private String captureUrl() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(httpClient).fetch(captor.capture());
        return captor.getValue();
    }
}






