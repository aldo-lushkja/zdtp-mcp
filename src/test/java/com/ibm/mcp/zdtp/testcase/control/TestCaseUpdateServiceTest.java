package com.ibm.mcp.zdtp.testcase.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.control.TargetProcessApiException;
import com.ibm.mcp.zdtp.testcase.control.TestCaseConverter;
import com.ibm.mcp.zdtp.testcase.entity.TestCaseDto;
import com.ibm.mcp.zdtp.testcase.entity.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestCaseUpdateServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    // 1736899200000 ms epoch = 2025-01-15T00:00:00Z
    private static final String TEST_CASE_RESPONSE = """
            {"Id":77,"Name":"Updated test case","Project":{"Id":1,"Name":"satispay_plus"},
            "EntityState":{"Id":11,"Name":"In Progress"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "Owner":{"Id":5,"Login":"aldo.lushkja@satispay.com"},
            "TestPlans":{"Items":[{"Id":55,"Name":"Regression Suite"}]}}
            """;

    @Mock TargetProcessHttpClient httpClient;

    TestCaseUpdateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        service = new TestCaseUpdateService(props, httpClient, new TestCaseConverter(), new ObjectMapper());
    }

    // ── URL tests ─────────────────────────────────────────────────────────────────

    @Test
    void update_urlContainsTestCaseId() {
        givenApiReturns(TEST_CASE_RESPONSE);

        service.updateTestCase(77, null, null, null);

        assertThat(captureUrl()).contains("/api/v1/TestCases/77");
    }

    @Test
    void update_urlContainsFormatJson() {
        givenApiReturns(TEST_CASE_RESPONSE);

        service.updateTestCase(77, null, null, null);

        assertThat(captureUrl()).contains("format=json");
    }

    @Test
    void update_urlContainsInclude() {
        givenApiReturns(TEST_CASE_RESPONSE);

        service.updateTestCase(77, null, null, null);

        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8))
                .contains("Owner");
    }

    // ── Request body tests ────────────────────────────────────────────────────────

    @Test
    void update_bodyContainsNameWhenProvided() {
        givenApiReturns(TEST_CASE_RESPONSE);

        service.updateTestCase(77, "Updated test case", null, null);

        assertThat(captureBody()).contains("\"Name\":\"Updated test case\"");
    }

    @Test
    void update_bodyOmitsNameWhenBlank() {
        givenApiReturns(TEST_CASE_RESPONSE);

        service.updateTestCase(77, "", null, null);

        assertThat(captureBody()).doesNotContain("Name");
    }

    @Test
    void update_bodyContainsDescriptionWhenProvided() {
        givenApiReturns(TEST_CASE_RESPONSE);

        service.updateTestCase(77, null, "Updated description", null);

        assertThat(captureBody()).contains("\"Description\":\"Updated description\"");
    }

    @Test
    void update_bodyContainsEntityStateWhenProvided() {
        givenApiReturns(TEST_CASE_RESPONSE);

        service.updateTestCase(77, null, null, "In Progress");

        String body = captureBody();
        assertThat(body).contains("EntityState");
        assertThat(body).contains("\"Name\":\"In Progress\"");
    }

    @Test
    void update_bodyOmitsEntityStateWhenBlank() {
        givenApiReturns(TEST_CASE_RESPONSE);

        service.updateTestCase(77, null, null, "");

        assertThat(captureBody()).doesNotContain("EntityState");
    }

    @Test
    void update_emptyBody_whenAllFieldsNull() {
        givenApiReturns(TEST_CASE_RESPONSE);

        service.updateTestCase(77, null, null, null);

        assertThat(captureBody()).isEqualTo("{}");
    }

    // ── Response parsing tests ────────────────────────────────────────────────────

    @Test
    void update_mapsResponseCorrectly() {
        givenApiReturns(TEST_CASE_RESPONSE);

        TestCaseDto result = service.updateTestCase(77, "Updated test case", null, "In Progress");

        assertThat(result.id()).isEqualTo(77);
        assertThat(result.name()).isEqualTo("Updated test case");
        assertThat(result.state()).isEqualTo("In Progress");
        assertThat(result.testPlanName()).isEqualTo("Regression Suite");
        assertThat(result.createdAt()).isEqualTo("2025-01-15");
    }

    @Test
    void update_apiError_throwsTargetProcessApiException() {
        when(httpClient.post(any(), any()))
                .thenThrow(new TargetProcessApiException(404, "{\"Message\":\"Not Found\"}"));

        assertThatThrownBy(() -> service.updateTestCase(999, "X", null, null))
                .isInstanceOf(TargetProcessApiException.class);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────────

    private void givenApiReturns(String body) {
        when(httpClient.post(any(), any())).thenReturn(body);
        when(httpClient.parseSingle(eq(body), eq(TestCase.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(body.trim(), TestCase.class));
    }

    private String captureUrl() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(httpClient).post(captor.capture(), any());
        return captor.getValue();
    }

    private String captureBody() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(httpClient).post(any(), captor.capture());
        return captor.getValue();
    }
}