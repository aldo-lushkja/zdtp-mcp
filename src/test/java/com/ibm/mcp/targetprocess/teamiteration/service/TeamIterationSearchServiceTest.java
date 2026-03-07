package com.ibm.mcp.targetprocess.teamiteration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.shared.exception.TargetProcessApiException;
import com.ibm.mcp.targetprocess.teamiteration.converter.TeamIterationConverter;
import com.ibm.mcp.targetprocess.teamiteration.dto.TeamIterationDto;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamIterationSearchServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    private static final String EMPTY_RESPONSE = "{\"Items\":[]}";
    // 1740268800000 = 2025-02-23T00:00:00Z, 1741564799000 = 2025-03-09T23:59:59Z
    private static final String ITERATION_RESPONSE = """
            {"Items":[{"Id":213616,"Name":"IT Consumer 6 2026-2-23",
            "StartDate":"\\/Date(1740268800000+0000)\\/",
            "EndDate":"\\/Date(1741564799000+0000)\\/",
            "Team":{"Id":163894,"Name":"IT Consumer 6"}}]}
            """;

    @Mock
    HttpClient httpClient;

    @SuppressWarnings("unchecked")
    @Mock
    HttpResponse<String> httpResponse;

    TeamIterationSearchService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        TargetProcessHttpClient tpHttpClient = new TargetProcessHttpClient(httpClient, new ObjectMapper());
        service = new TeamIterationSearchService(props, tpHttpClient, new TeamIterationConverter());
    }

    // ── URL / where clause tests ────────────────────────────────────────────────

    @Test
    void noFilters_producesEmptyWhereClause() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTeamIterations("", null, "", null, null, 10);

        String url = captureDecodedUrl();
        assertThat(url).contains("/api/v1/TeamIterations");
        assertThat(urlParam(url, "where")).isEmpty();
    }

    @Test
    void nameFilter_producesNameContainsCondition() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTeamIterations("2026", null, "", null, null, 10);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("Name contains '2026'");
    }

    @Test
    void teamIdFilter_producesTeamIdEqCondition() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTeamIterations("", 163894, "", null, null, 10);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("Team.Id eq 163894");
    }

    @Test
    void teamNameFilter_producesTeamNameContainsCondition() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTeamIterations("", null, "Consumer 6", null, null, 10);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("Team.Name contains 'Consumer 6'");
    }

    @Test
    void startDateFilter_usesGteOnStartDate() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTeamIterations("", null, "", "2026-01-01", null, 10);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("StartDate gte '2026-01-01'");
    }

    @Test
    void endDateFilter_usesLtOnStartDate() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTeamIterations("", null, "", null, "2026-12-31", 10);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("StartDate lt '2026-12-31'");
    }

    @Test
    void allFilters_combinedWithAnd() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTeamIterations("2026", 163894, "Consumer 6", "2026-01-01", "2026-12-31", 10);

        String where = urlParam(captureDecodedUrl(), "where");
        assertThat(where)
                .contains("Name contains '2026'")
                .contains("Team.Id eq 163894")
                .contains("Team.Name contains 'Consumer 6'")
                .contains("StartDate gte '2026-01-01'")
                .contains("StartDate lt '2026-12-31'")
                .contains(" and ");
    }

    @Test
    void takeParameter_isIncludedInUrl() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTeamIterations("", null, "", null, null, 5);

        assertThat(captureDecodedUrl()).contains("take=5");
    }

    @Test
    void resultOrderedByStartDateDesc() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTeamIterations("", null, "", null, null, 10);

        assertThat(captureDecodedUrl()).contains("orderByDesc=StartDate");
    }

    // ── Result parsing tests ────────────────────────────────────────────────────

    @Test
    void emptyItemsArray_returnsEmptyList() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        List<TeamIterationDto> result = service.searchTeamIterations("", null, "", null, null, 10);

        assertThat(result).isEmpty();
    }

    @Test
    void validResponse_mapsFieldsCorrectly() throws Exception {
        givenApiReturns(ITERATION_RESPONSE);

        List<TeamIterationDto> result = service.searchTeamIterations("", 163894, "", null, null, 10);

        assertThat(result).hasSize(1);
        TeamIterationDto it = result.get(0);
        assertThat(it.id()).isEqualTo(213616);
        assertThat(it.name()).isEqualTo("IT Consumer 6 2026-2-23");
        assertThat(it.teamId()).isEqualTo(163894);
        assertThat(it.teamName()).isEqualTo("IT Consumer 6");
        assertThat(it.startDate()).isEqualTo("2025-02-23");
        assertThat(it.endDate()).isEqualTo("2025-03-09");
    }

    @Test
    void nonOkHttpStatus_throwsTargetProcessApiException() throws Exception {
        when(httpResponse.statusCode()).thenReturn(400);
        when(httpResponse.body()).thenReturn("{\"Message\":\"Bad Request\"}");
        doReturn(httpResponse).when(httpClient).send(any(), any());

        assertThatThrownBy(() -> service.searchTeamIterations("", null, "", null, null, 10))
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

    private String urlParam(String url, String param) {
        return java.util.Arrays.stream(url.split("[?&]"))
                .filter(p -> p.startsWith(param + "="))
                .map(p -> p.substring(param.length() + 1))
                .findFirst()
                .orElse("");
    }
}