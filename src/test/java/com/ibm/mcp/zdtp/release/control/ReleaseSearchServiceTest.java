package com.ibm.mcp.zdtp.release.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.release.control.ReleaseConverter;
import com.ibm.mcp.zdtp.release.entity.ReleaseDto;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.control.TargetProcessApiException;
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
class ReleaseSearchServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    private static final String EMPTY_RESPONSE = "{\"Items\":[]}";
    // 1736899200000 ms epoch = 2025-01-15T00:00:00Z
    // 1739577600000 ms epoch = 2025-02-15T00:00:00Z
    // 1742860800000 ms epoch = 2025-03-25T00:00:00Z
    private static final String RELEASE_RESPONSE = """
            {"Items":[{"Id":77,"Name":"Release 1.0","Project":{"Id":1,"Name":"satispay_plus"},
            "EntityState":{"Id":10,"Name":"Open"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "StartDate":"\\/Date(1739577600000+0000)\\/","EndDate":"\\/Date(1742860800000+0000)\\/","Effort":5.0,
            "Owner":{"Id":5,"Login":"aldo.lushkja@satispay.com"}}]}
            """;

    @Mock
    HttpClient httpClient;

    @SuppressWarnings("unchecked")
    @Mock
    HttpResponse<String> httpResponse;

    ReleaseSearchService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        TargetProcessHttpClient tpHttpClient = new TargetProcessHttpClient(httpClient, new ObjectMapper());
        service = new ReleaseSearchService(props, tpHttpClient, new ReleaseConverter());
    }

    // ── URL / where clause tests ────────────────────────────────────────────────

    @Test
    void noFilters_producesEmptyWhereClause() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchReleases("", "", "", "", "", 10, null);

        String url = captureDecodedUrl();
        assertThat(url).contains("/api/v1/Releases");
        assertThat(urlParam(url, "where")).isEmpty();
    }

    @Test
    void nameFilter_producesNameContainsCondition() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchReleases("Release 1", "", "", "", "", 10, null);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("Name contains 'Release 1'");
    }

    @Test
    void projectFilter_producesProjectNameContainsCondition() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchReleases("", "satispay_plus", "", "", "", 10, null);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("Project.Name contains 'satispay_plus'");
    }

    @Test
    void ownerLoginFilter_usesOwnerLoginProperty() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchReleases("", "", "aldo.lushkja@satispay.com", "", "", 10, null);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("Owner.Login eq 'aldo.lushkja@satispay.com'");
    }

    @Test
    void startDateFilter_usesStartDateGteOperator() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchReleases("", "", "", "2026-01-01", "", 10, null);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("StartDate gte '2026-01-01'");
    }

    @Test
    void endDateFilter_usesStartDateLtOperator() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchReleases("", "", "", "", "2026-12-31", 10, null);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("StartDate lt '2026-12-31'");
    }

    @Test
    void allFilters_combinedWithAnd() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchReleases("Release 1", "satispay_plus", "aldo.lushkja@satispay.com", "2026-01-01", "2026-12-31", 10, null);

        String where = urlParam(captureDecodedUrl(), "where");
        assertThat(where)
                .contains("Name contains 'Release 1'")
                .contains("Project.Name contains 'satispay_plus'")
                .contains("Owner.Login eq 'aldo.lushkja@satispay.com'")
                .contains("StartDate gte '2026-01-01'")
                .contains("StartDate lt '2026-12-31'")
                .contains(" and ");
    }

    @Test
    void includeClause_containsStartDateAndEndDate() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchReleases("", "", "", "", "", 10, null);

        String url = captureDecodedUrl();
        assertThat(url).contains("StartDate").contains("EndDate");
    }

    @Test
    void takeParameter_isIncludedInUrl() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchReleases("", "", "", "", "", 25, null);

        assertThat(captureDecodedUrl()).contains("take=25");
    }

    // ── Team iteration filter tests ──────────────────────────────────────────────

    @Test
    void teamIterationIdFilter_addsTeamIterationsIdEqCondition() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchReleases("", "", "", "", "", 10, 213616);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("TeamIterations.Id eq 213616");
    }

    @Test
    void teamIterationIdFilter_nullValue_noCondition() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchReleases("", "", "", "", "", 10, null);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .doesNotContain("TeamIterations.Id");
    }

    @Test
    void teamIterationIdFilter_combinedWithProjectFilter() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchReleases("", "consumer_loyalty", "", "", "", 10, 213616);

        String where = urlParam(captureDecodedUrl(), "where");
        assertThat(where)
                .contains("Project.Name contains 'consumer_loyalty'")
                .contains("TeamIterations.Id eq 213616")
                .contains(" and ");
    }

    // ── Result parsing tests ────────────────────────────────────────────────────

    @Test
    void emptyItemsArray_returnsEmptyList() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        List<ReleaseDto> result = service.searchReleases("", "", "", "", "", 10, null);

        assertThat(result).isEmpty();
    }

    @Test
    void validResponse_mapsFieldsCorrectly() throws Exception {
        givenApiReturns(RELEASE_RESPONSE);

        List<ReleaseDto> result = service.searchReleases("", "", "", "", "", 10, null);

        assertThat(result).hasSize(1);
        ReleaseDto release = result.get(0);
        assertThat(release.id()).isEqualTo(77);
        assertThat(release.name()).isEqualTo("Release 1.0");
        assertThat(release.projectName()).isEqualTo("satispay_plus");
        assertThat(release.state()).isEqualTo("Open");
        assertThat(release.ownerLogin()).isEqualTo("aldo.lushkja@satispay.com");
        assertThat(release.effort()).isEqualTo(5.0);
        assertThat(release.createdAt()).isEqualTo("2025-01-15");
        assertThat(release.startDate()).isEqualTo("2025-02-15");
        assertThat(release.endDate()).isEqualTo("2025-03-25");
    }

    @Test
    void nonOkHttpStatus_throwsTargetProcessApiException() throws Exception {
        when(httpResponse.statusCode()).thenReturn(400);
        when(httpResponse.body()).thenReturn("{\"Message\":\"Bad Request\"}");
        doReturn(httpResponse).when(httpClient).send(any(), any());

        assertThatThrownBy(() -> service.searchReleases("", "", "", "", "", 10, null))
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