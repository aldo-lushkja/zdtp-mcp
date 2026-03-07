package com.ibm.mcp.zdtp.testplan.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.control.TargetProcessApiException;
import com.ibm.mcp.zdtp.testplan.control.TestPlanConverter;
import com.ibm.mcp.zdtp.testplan.entity.TestPlanDto;
import com.ibm.mcp.zdtp.testplan.entity.TestPlan;
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
class TestPlanCreateServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    // 1736899200000 ms epoch = 2025-01-15T00:00:00Z
    private static final String TEST_PLAN_RESPONSE = """
            {"Id":300,"Name":"Smoke Tests","Project":{"Id":42,"Name":"satispay_plus"},
            "EntityState":{"Id":10,"Name":"Open"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "Owner":{"Id":5,"Login":"aldo.lushkja@satispay.com"}}
            """;

    @Mock TargetProcessHttpClient httpClient;

    TestPlanCreateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        service = new TestPlanCreateService(props, httpClient, new TestPlanConverter(), new ObjectMapper());
    }

    // ── URL tests ─────────────────────────────────────────────────────────────────

    @Test
    void create_urlPointsToTestPlansEndpoint() {
        givenApiReturns(TEST_PLAN_RESPONSE);

        service.createTestPlan("Smoke Tests", 42, null);

        assertThat(captureUrl()).contains(BASE_URL + "/api/v1/TestPlans");
    }

    @Test
    void create_urlContainsFormatJson() {
        givenApiReturns(TEST_PLAN_RESPONSE);

        service.createTestPlan("Smoke Tests", 42, null);

        assertThat(captureUrl()).contains("format=json");
    }

    @Test
    void create_urlContainsInclude() {
        givenApiReturns(TEST_PLAN_RESPONSE);

        service.createTestPlan("Smoke Tests", 42, null);

        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8))
                .contains("Owner");
    }

    // ── Request body tests ────────────────────────────────────────────────────────

    @Test
    void create_bodyContainsNameAndProjectId() {
        givenApiReturns(TEST_PLAN_RESPONSE);

        service.createTestPlan("Smoke Tests", 42, null);

        String body = captureBody();
        assertThat(body).contains("\"Name\":\"Smoke Tests\"");
        assertThat(body).contains("\"Id\":42");
    }

    @Test
    void create_bodyContainsDescriptionWhenProvided() {
        givenApiReturns(TEST_PLAN_RESPONSE);

        service.createTestPlan("Smoke Tests", 42, "Covers critical paths");

        assertThat(captureBody()).contains("\"Description\":\"Covers critical paths\"");
    }

    @Test
    void create_bodyOmitsDescriptionWhenBlank() {
        givenApiReturns(TEST_PLAN_RESPONSE);

        service.createTestPlan("Smoke Tests", 42, "");

        assertThat(captureBody()).doesNotContain("Description");
    }

    @Test
    void create_bodyOmitsDescriptionWhenNull() {
        givenApiReturns(TEST_PLAN_RESPONSE);

        service.createTestPlan("Smoke Tests", 42, null);

        assertThat(captureBody()).doesNotContain("Description");
    }

    // ── Response parsing tests ────────────────────────────────────────────────────

    @Test
    void create_mapsResponseCorrectly() {
        givenApiReturns(TEST_PLAN_RESPONSE);

        TestPlanDto result = service.createTestPlan("Smoke Tests", 42, null);

        assertThat(result.id()).isEqualTo(300);
        assertThat(result.name()).isEqualTo("Smoke Tests");
        assertThat(result.projectName()).isEqualTo("satispay_plus");
        assertThat(result.state()).isEqualTo("Open");
        assertThat(result.ownerLogin()).isEqualTo("aldo.lushkja@satispay.com");
        assertThat(result.createdAt()).isEqualTo("2025-01-15");
    }

    @Test
    void create_apiError_throwsTargetProcessApiException() {
        when(httpClient.post(any(), any()))
                .thenThrow(new TargetProcessApiException(400, "{\"Message\":\"Bad Request\"}"));

        assertThatThrownBy(() -> service.createTestPlan("Bad", 0, null))
                .isInstanceOf(TargetProcessApiException.class);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────────

    private void givenApiReturns(String body) {
        when(httpClient.post(any(), any())).thenReturn(body);
        when(httpClient.parseSingle(eq(body), eq(TestPlan.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(body.trim(), TestPlan.class));
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