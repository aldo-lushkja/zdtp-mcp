package com.ibm.mcp.zdtp.teamiteration.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.control.TargetProcessApiException;
import com.ibm.mcp.zdtp.teamiteration.control.TeamIterationConverter;
import com.ibm.mcp.zdtp.teamiteration.entity.TeamIterationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamIterationGetByIdServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    // 1740268800000 = 2025-02-23T00:00:00Z, 1741564799000 = 2025-03-09T23:59:59Z
    private static final String ITERATION_RESPONSE = """
            {"Id":213616,"Name":"IT Consumer 6 2026-2-23",
            "StartDate":"\\/Date(1740268800000+0000)\\/",
            "EndDate":"\\/Date(1741564799000+0000)\\/",
            "Team":{"Id":163894,"Name":"IT Consumer 6"}}
            """;

    @Mock
    HttpClient httpClient;

    @SuppressWarnings("unchecked")
    @Mock
    HttpResponse<String> httpResponse;

    TeamIterationGetByIdService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        TargetProcessHttpClient tpHttpClient = new TargetProcessHttpClient(httpClient, new ObjectMapper());
        service = new TeamIterationGetByIdService(props, tpHttpClient, new TeamIterationConverter());
    }

    @Test
    void urlContainsEntityId() throws Exception {
        givenApiReturns(ITERATION_RESPONSE);

        service.getById(213616);

        assertThat(captureDecodedUrl()).contains("/TeamIterations/213616");
    }

    @Test
    void urlContainsRequiredIncludeFields() throws Exception {
        givenApiReturns(ITERATION_RESPONSE);

        service.getById(213616);

        String url = captureDecodedUrl();
        assertThat(url)
                .contains("StartDate")
                .contains("EndDate")
                .contains("Team");
    }

    @Test
    void validResponse_mapsFieldsCorrectly() throws Exception {
        givenApiReturns(ITERATION_RESPONSE);

        TeamIterationDto it = service.getById(213616);

        assertThat(it.id()).isEqualTo(213616);
        assertThat(it.name()).isEqualTo("IT Consumer 6 2026-2-23");
        assertThat(it.teamId()).isEqualTo(163894);
        assertThat(it.teamName()).isEqualTo("IT Consumer 6");
        assertThat(it.startDate()).isEqualTo("2025-02-23");
        assertThat(it.endDate()).isEqualTo("2025-03-09");
    }

    @Test
    void nonOkHttpStatus_throwsTargetProcessApiException() throws Exception {
        when(httpResponse.statusCode()).thenReturn(404);
        when(httpResponse.body()).thenReturn("{\"Message\":\"Not Found\"}");
        doReturn(httpResponse).when(httpClient).send(any(), any());

        assertThatThrownBy(() -> service.getById(999))
                .isInstanceOf(TargetProcessApiException.class);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────

    private void givenApiReturns(String body) throws IOException, InterruptedException {
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(body);
        doReturn(httpResponse).when(httpClient).send(any(), any());
    }

    private String captureDecodedUrl() throws IOException, InterruptedException {
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(captor.capture(), any());
        return URLDecoder.decode(captor.getValue().uri().toString(), StandardCharsets.UTF_8);
    }
}