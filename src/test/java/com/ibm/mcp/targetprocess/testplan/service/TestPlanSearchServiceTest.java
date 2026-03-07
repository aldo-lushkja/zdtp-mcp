package com.ibm.mcp.targetprocess.testplan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.shared.exception.TargetProcessApiException;
import com.ibm.mcp.targetprocess.testplan.converter.TestPlanConverter;
import com.ibm.mcp.targetprocess.testplan.dto.TestPlanDto;
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
class TestPlanSearchServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    private static final String EMPTY_RESPONSE = "{\"Items\":[]}";
    // 1736899200000 ms epoch = 2025-01-15T00:00:00Z
    private static final String TEST_PLAN_RESPONSE = """
            {"Items":[{"Id":55,"Name":"Regression Suite","Project":{"Id":1,"Name":"satispay_plus"},
            "EntityState":{"Id":10,"Name":"Open"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "Owner":{"Id":5,"Login":"aldo.lushkja@satispay.com"}}]}
            """;

    @Mock HttpClient httpClient;
    @SuppressWarnings("unchecked") @Mock HttpResponse<String> httpResponse;

    TestPlanSearchService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        TargetProcessHttpClient tpHttpClient = new TargetProcessHttpClient(httpClient, new ObjectMapper());
        service = new TestPlanSearchService(props, tpHttpClient, new TestPlanConverter());
    }

    // ── URL / where clause tests ──────────────────────────────────────────────────

    @Test
    void noFilters_urlPointsToTestPlansEndpoint() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTestPlans("", "", "", "", "", 10);

        assertThat(captureDecodedUrl()).contains("/api/v1/TestPlans");
    }

    @Test
    void noFilters_producesEmptyWhereClause() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTestPlans("", "", "", "", "", 10);

        assertThat(urlParam(captureDecodedUrl(), "where")).isEmpty();
    }

    @Test
    void nameFilter_producesNameContainsCondition() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTestPlans("regression", "", "", "", "", 10);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("Name contains 'regression'");
    }

    @Test
    void projectFilter_producesProjectNameContainsCondition() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTestPlans("", "satispay_plus", "", "", "", 10);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("Project.Name contains 'satispay_plus'");
    }

    @Test
    void ownerLoginFilter_usesOwnerLoginProperty() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTestPlans("", "", "aldo.lushkja@satispay.com", "", "", 10);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("Owner.Login eq 'aldo.lushkja@satispay.com'");
    }

    @Test
    void startDateFilter_usesGteOperator() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTestPlans("", "", "", "2026-01-01", "", 10);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("CreateDate gte '2026-01-01'");
    }

    @Test
    void endDateFilter_usesLtOperator() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTestPlans("", "", "", "", "2026-12-31", 10);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("CreateDate lt '2026-12-31'");
    }

    @Test
    void allFilters_combinedWithAnd() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTestPlans("regression", "satispay_plus", "aldo.lushkja@satispay.com", "2026-01-01", "2026-12-31", 10);

        String where = urlParam(captureDecodedUrl(), "where");
        assertThat(where)
                .contains("Name contains 'regression'")
                .contains("Project.Name contains 'satispay_plus'")
                .contains("Owner.Login eq 'aldo.lushkja@satispay.com'")
                .contains("CreateDate gte '2026-01-01'")
                .contains("CreateDate lt '2026-12-31'")
                .contains(" and ");
    }

    @Test
    void takeParameter_isIncludedInUrl() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTestPlans("", "", "", "", "", 20);

        assertThat(captureDecodedUrl()).contains("take=20");
    }

    // ── Result parsing tests ──────────────────────────────────────────────────────

    @Test
    void emptyItemsArray_returnsEmptyList() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        List<TestPlanDto> result = service.searchTestPlans("", "", "", "", "", 10);

        assertThat(result).isEmpty();
    }

    @Test
    void validResponse_mapsFieldsCorrectly() throws Exception {
        givenApiReturns(TEST_PLAN_RESPONSE);

        List<TestPlanDto> result = service.searchTestPlans("", "", "", "", "", 10);

        assertThat(result).hasSize(1);
        TestPlanDto tp = result.get(0);
        assertThat(tp.id()).isEqualTo(55);
        assertThat(tp.name()).isEqualTo("Regression Suite");
        assertThat(tp.projectName()).isEqualTo("satispay_plus");
        assertThat(tp.state()).isEqualTo("Open");
        assertThat(tp.ownerLogin()).isEqualTo("aldo.lushkja@satispay.com");
        assertThat(tp.createdAt()).isEqualTo("2025-01-15");
    }

    @Test
    void nonOkHttpStatus_throwsTargetProcessApiException() throws Exception {
        when(httpResponse.statusCode()).thenReturn(400);
        when(httpResponse.body()).thenReturn("{\"Message\":\"Bad Request\"}");
        doReturn(httpResponse).when(httpClient).send(any(), any());

        assertThatThrownBy(() -> service.searchTestPlans("", "", "", "", "", 10))
                .isInstanceOf(TargetProcessApiException.class);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────────

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