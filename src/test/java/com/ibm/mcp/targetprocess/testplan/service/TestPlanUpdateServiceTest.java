package com.ibm.mcp.targetprocess.testplan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.shared.exception.TargetProcessApiException;
import com.ibm.mcp.targetprocess.testplan.converter.TestPlanConverter;
import com.ibm.mcp.targetprocess.testplan.dto.TestPlanDto;
import com.ibm.mcp.targetprocess.testplan.model.TestPlan;
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
class TestPlanUpdateServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    // 1736899200000 ms epoch = 2025-01-15T00:00:00Z
    private static final String TEST_PLAN_RESPONSE = """
            {"Id":55,"Name":"Updated Plan","Project":{"Id":1,"Name":"satispay_plus"},
            "EntityState":{"Id":11,"Name":"In Progress"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "Owner":{"Id":5,"Login":"aldo.lushkja@satispay.com"}}
            """;

    @Mock TargetProcessHttpClient httpClient;

    TestPlanUpdateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        service = new TestPlanUpdateService(props, httpClient, new TestPlanConverter(), new ObjectMapper());
    }

    // ── URL tests ─────────────────────────────────────────────────────────────────

    @Test
    void update_urlContainsTestPlanId() {
        givenApiReturns(TEST_PLAN_RESPONSE);

        service.updateTestPlan(55, null, null, null);

        assertThat(captureUrl()).contains("/api/v1/TestPlans/55");
    }

    @Test
    void update_urlContainsFormatJson() {
        givenApiReturns(TEST_PLAN_RESPONSE);

        service.updateTestPlan(55, null, null, null);

        assertThat(captureUrl()).contains("format=json");
    }

    @Test
    void update_urlContainsInclude() {
        givenApiReturns(TEST_PLAN_RESPONSE);

        service.updateTestPlan(55, null, null, null);

        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8))
                .contains("Owner");
    }

    // ── Request body tests ────────────────────────────────────────────────────────

    @Test
    void update_bodyContainsNameWhenProvided() {
        givenApiReturns(TEST_PLAN_RESPONSE);

        service.updateTestPlan(55, "Updated Plan", null, null);

        assertThat(captureBody()).contains("\"Name\":\"Updated Plan\"");
    }

    @Test
    void update_bodyOmitsNameWhenBlank() {
        givenApiReturns(TEST_PLAN_RESPONSE);

        service.updateTestPlan(55, "", null, null);

        assertThat(captureBody()).doesNotContain("Name");
    }

    @Test
    void update_bodyContainsDescriptionWhenProvided() {
        givenApiReturns(TEST_PLAN_RESPONSE);

        service.updateTestPlan(55, null, "Updated description", null);

        assertThat(captureBody()).contains("\"Description\":\"Updated description\"");
    }

    @Test
    void update_bodyContainsEntityStateWhenProvided() {
        givenApiReturns(TEST_PLAN_RESPONSE);

        service.updateTestPlan(55, null, null, "In Progress");

        String body = captureBody();
        assertThat(body).contains("EntityState");
        assertThat(body).contains("\"Name\":\"In Progress\"");
    }

    @Test
    void update_bodyOmitsEntityStateWhenBlank() {
        givenApiReturns(TEST_PLAN_RESPONSE);

        service.updateTestPlan(55, null, null, "");

        assertThat(captureBody()).doesNotContain("EntityState");
    }

    @Test
    void update_emptyBody_whenAllFieldsNull() {
        givenApiReturns(TEST_PLAN_RESPONSE);

        service.updateTestPlan(55, null, null, null);

        assertThat(captureBody()).isEqualTo("{}");
    }

    // ── Response parsing tests ────────────────────────────────────────────────────

    @Test
    void update_mapsResponseCorrectly() {
        givenApiReturns(TEST_PLAN_RESPONSE);

        TestPlanDto result = service.updateTestPlan(55, "Updated Plan", null, "In Progress");

        assertThat(result.id()).isEqualTo(55);
        assertThat(result.name()).isEqualTo("Updated Plan");
        assertThat(result.state()).isEqualTo("In Progress");
        assertThat(result.createdAt()).isEqualTo("2025-01-15");
    }

    @Test
    void update_apiError_throwsTargetProcessApiException() {
        when(httpClient.post(any(), any()))
                .thenThrow(new TargetProcessApiException(404, "{\"Message\":\"Not Found\"}"));

        assertThatThrownBy(() -> service.updateTestPlan(999, "X", null, null))
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