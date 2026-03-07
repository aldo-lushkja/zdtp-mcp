package com.ibm.mcp.targetprocess.testcase.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.shared.exception.TargetProcessApiException;
import com.ibm.mcp.targetprocess.testcase.converter.TestCaseConverter;
import com.ibm.mcp.targetprocess.testcase.dto.TestCaseDto;
import com.ibm.mcp.targetprocess.testcase.model.TestCase;
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
class TestCaseCreateServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    // 1736899200000 ms epoch = 2025-01-15T00:00:00Z
    private static final String TEST_CASE_RESPONSE = """
            {"Id":77,"Name":"Login flow test","Project":{"Id":42,"Name":"satispay_plus"},
            "EntityState":{"Id":10,"Name":"Open"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "Owner":{"Id":5,"Login":"aldo.lushkja@satispay.com"},
            "TestPlans":{"Items":[{"Id":55,"Name":"Regression Suite"}]}}
            """;

    @Mock TargetProcessHttpClient httpClient;

    TestCaseCreateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        service = new TestCaseCreateService(props, httpClient, new TestCaseConverter(), new ObjectMapper());
    }

    // ── URL tests ─────────────────────────────────────────────────────────────────

    @Test
    void create_urlPointsToTestCasesEndpoint() {
        givenApiReturns(TEST_CASE_RESPONSE);

        service.createTestCase("Login flow test", 42, null, null);

        assertThat(captureUrl()).contains(BASE_URL + "/api/v1/TestCases");
    }

    @Test
    void create_urlContainsFormatJson() {
        givenApiReturns(TEST_CASE_RESPONSE);

        service.createTestCase("Login flow test", 42, null, null);

        assertThat(captureUrl()).contains("format=json");
    }

    @Test
    void create_urlContainsInclude() {
        givenApiReturns(TEST_CASE_RESPONSE);

        service.createTestCase("Login flow test", 42, null, null);

        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8))
                .contains("Owner");
    }

    // ── Request body tests ────────────────────────────────────────────────────────

    @Test
    void create_bodyContainsNameAndProjectId() {
        givenApiReturns(TEST_CASE_RESPONSE);

        service.createTestCase("Login flow test", 42, null, null);

        String body = captureBody();
        assertThat(body).contains("\"Name\":\"Login flow test\"");
        assertThat(body).contains("\"Id\":42");
    }

    @Test
    void create_bodyContainsDescriptionWhenProvided() {
        givenApiReturns(TEST_CASE_RESPONSE);

        service.createTestCase("Login flow test", 42, "Verify login works", null);

        assertThat(captureBody()).contains("\"Description\":\"Verify login works\"");
    }

    @Test
    void create_bodyOmitsDescriptionWhenBlank() {
        givenApiReturns(TEST_CASE_RESPONSE);

        service.createTestCase("Login flow test", 42, "", null);

        assertThat(captureBody()).doesNotContain("Description");
    }

    @Test
    void create_bodyOmitsDescriptionWhenNull() {
        givenApiReturns(TEST_CASE_RESPONSE);

        service.createTestCase("Login flow test", 42, null, null);

        assertThat(captureBody()).doesNotContain("Description");
    }

    @Test
    void create_bodyContainsTestPlanIdWhenProvided() {
        givenApiReturns(TEST_CASE_RESPONSE);

        service.createTestCase("Login flow test", 42, null, 55);

        String body = captureBody();
        assertThat(body).contains("TestPlan");
        assertThat(body).contains("\"Id\":55");
    }

    @Test
    void create_bodyOmitsTestPlanWhenNull() {
        givenApiReturns(TEST_CASE_RESPONSE);

        service.createTestCase("Login flow test", 42, null, null);

        assertThat(captureBody()).doesNotContain("TestPlan");
    }

    // ── Response parsing tests ────────────────────────────────────────────────────

    @Test
    void create_mapsResponseCorrectly() {
        givenApiReturns(TEST_CASE_RESPONSE);

        TestCaseDto result = service.createTestCase("Login flow test", 42, null, 55);

        assertThat(result.id()).isEqualTo(77);
        assertThat(result.name()).isEqualTo("Login flow test");
        assertThat(result.projectName()).isEqualTo("satispay_plus");
        assertThat(result.state()).isEqualTo("Open");
        assertThat(result.ownerLogin()).isEqualTo("aldo.lushkja@satispay.com");
        assertThat(result.testPlanName()).isEqualTo("Regression Suite");
        assertThat(result.createdAt()).isEqualTo("2025-01-15");
    }

    @Test
    void create_apiError_throwsTargetProcessApiException() {
        when(httpClient.post(any(), any()))
                .thenThrow(new TargetProcessApiException(400, "{\"Message\":\"Bad Request\"}"));

        assertThatThrownBy(() -> service.createTestCase("Bad", 0, null, null))
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