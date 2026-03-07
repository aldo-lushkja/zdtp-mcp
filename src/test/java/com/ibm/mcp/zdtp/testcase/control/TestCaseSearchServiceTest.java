package com.ibm.mcp.zdtp.testcase.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.control.TargetProcessApiException;
import com.ibm.mcp.zdtp.testcase.control.TestCaseConverter;
import com.ibm.mcp.zdtp.testcase.entity.TestCaseDto;
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
class TestCaseSearchServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    private static final String EMPTY_RESPONSE = "{\"Items\":[]}";
    // 1736899200000 ms epoch = 2025-01-15T00:00:00Z
    private static final String TEST_CASE_RESPONSE = """
            {"Items":[{"Id":77,"Name":"Login flow test","Project":{"Id":1,"Name":"satispay_plus"},
            "EntityState":{"Id":10,"Name":"Open"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "Owner":{"Id":5,"Login":"aldo.lushkja@satispay.com"},
            "TestPlans":{"Items":[{"Id":55,"Name":"Regression Suite"}]}}]}
            """;

    @Mock HttpClient httpClient;
    @SuppressWarnings("unchecked") @Mock HttpResponse<String> httpResponse;

    TestCaseSearchService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        TargetProcessHttpClient tpHttpClient = new TargetProcessHttpClient(httpClient, new ObjectMapper());
        service = new TestCaseSearchService(props, tpHttpClient, new TestCaseConverter());
    }

    // ── URL / where clause tests ──────────────────────────────────────────────────

    @Test
    void noFilters_urlPointsToTestCasesEndpoint() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTestCases("", "", "", "", "", 10);

        assertThat(captureDecodedUrl()).contains("/api/v1/TestCases");
    }

    @Test
    void noFilters_producesEmptyWhereClause() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTestCases("", "", "", "", "", 10);

        assertThat(urlParam(captureDecodedUrl(), "where")).isEmpty();
    }

    @Test
    void nameFilter_producesNameContainsCondition() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTestCases("login", "", "", "", "", 10);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("Name contains 'login'");
    }

    @Test
    void projectFilter_producesProjectNameContainsCondition() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTestCases("", "satispay_plus", "", "", "", 10);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("Project.Name contains 'satispay_plus'");
    }

    @Test
    void ownerLoginFilter_usesOwnerLoginProperty() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTestCases("", "", "aldo.lushkja@satispay.com", "", "", 10);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("Owner.Login eq 'aldo.lushkja@satispay.com'");
    }

    @Test
    void startDateFilter_usesGteOperator() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTestCases("", "", "", "2026-01-01", "", 10);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("CreateDate gte '2026-01-01'");
    }

    @Test
    void endDateFilter_usesLtOperator() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTestCases("", "", "", "", "2026-12-31", 10);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("CreateDate lt '2026-12-31'");
    }

    @Test
    void allFilters_combinedWithAnd() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTestCases("login", "satispay_plus", "aldo.lushkja@satispay.com", "2026-01-01", "2026-12-31", 10);

        String where = urlParam(captureDecodedUrl(), "where");
        assertThat(where)
                .contains("Name contains 'login'")
                .contains("Project.Name contains 'satispay_plus'")
                .contains("Owner.Login eq 'aldo.lushkja@satispay.com'")
                .contains("CreateDate gte '2026-01-01'")
                .contains("CreateDate lt '2026-12-31'")
                .contains(" and ");
    }

    @Test
    void takeParameter_isIncludedInUrl() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchTestCases("", "", "", "", "", 20);

        assertThat(captureDecodedUrl()).contains("take=20");
    }

    // ── Result parsing tests ──────────────────────────────────────────────────────

    @Test
    void emptyItemsArray_returnsEmptyList() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        List<TestCaseDto> result = service.searchTestCases("", "", "", "", "", 10);

        assertThat(result).isEmpty();
    }

    @Test
    void validResponse_mapsFieldsCorrectly() throws Exception {
        givenApiReturns(TEST_CASE_RESPONSE);

        List<TestCaseDto> result = service.searchTestCases("", "", "", "", "", 10);

        assertThat(result).hasSize(1);
        TestCaseDto tc = result.get(0);
        assertThat(tc.id()).isEqualTo(77);
        assertThat(tc.name()).isEqualTo("Login flow test");
        assertThat(tc.projectName()).isEqualTo("satispay_plus");
        assertThat(tc.state()).isEqualTo("Open");
        assertThat(tc.ownerLogin()).isEqualTo("aldo.lushkja@satispay.com");
        assertThat(tc.testPlanName()).isEqualTo("Regression Suite");
        assertThat(tc.createdAt()).isEqualTo("2025-01-15");
    }

    @Test
    void nonOkHttpStatus_throwsTargetProcessApiException() throws Exception {
        when(httpResponse.statusCode()).thenReturn(400);
        when(httpResponse.body()).thenReturn("{\"Message\":\"Bad Request\"}");
        doReturn(httpResponse).when(httpClient).send(any(), any());

        assertThatThrownBy(() -> service.searchTestCases("", "", "", "", "", 10))
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