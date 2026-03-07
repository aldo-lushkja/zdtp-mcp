package com.ibm.mcp.zdtp.testcase.control;

import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.exception.TargetProcessApiException;
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
class TestCaseCreateServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    // 1736899200000 ms epoch = 2025-01-15T00:00:00Z
    private static final String CASE_RESPONSE = """
            {"Id":1,"Name":"New Case","Project":{"Id":42,"Name":"P1"},
            "EntityState":{"Id":2,"Name":"Draft"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "Owner":{"Id":5,"Login":"owner@test.com"}}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    TestCaseCreateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new TestCaseCreateService(engine, new TestCaseConverter());
    }

    @Test
    void create_urlPointsToTestCasesEndpoint() {
        givenApiReturns(CASE_RESPONSE);
        service.createTestCase("New Case", 42, null, null);
        assertThat(captureUrl()).contains("/api/v1/TestCases");
    }

    @Test
    void create_urlContainsFormatJson() {
        givenApiReturns(CASE_RESPONSE);
        service.createTestCase("New Case", 42, null, null);
        assertThat(captureUrl()).contains("format=json");
    }

    @Test
    void create_urlContainsInclude() {
        givenApiReturns(CASE_RESPONSE);
        service.createTestCase("New Case", 42, null, null);
        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8)).contains("Owner");
    }

    @Test
    void create_bodyContainsNameAndProjectId() {
        givenApiReturns(CASE_RESPONSE);
        service.createTestCase("New Case", 42, null, null);
        String body = captureBody();
        assertThat(body).contains("\"Name\":\"New Case\"");
        assertThat(body).contains("\"Id\":42");
    }

    @Test
    void create_bodyContainsDescriptionWhenProvided() {
        givenApiReturns(CASE_RESPONSE);
        service.createTestCase("New Case", 42, "Case description", null);
        assertThat(captureBody()).contains("\"Description\":\"Case description\"");
    }

    @Test
    void create_bodyOmitsDescriptionWhenBlank() {
        givenApiReturns(CASE_RESPONSE);
        service.createTestCase("New Case", 42, "", null);
        assertThat(captureBody()).doesNotContain("Description");
    }

    @Test
    void create_bodyOmitsDescriptionWhenNull() {
        givenApiReturns(CASE_RESPONSE);
        service.createTestCase("New Case", 42, null, null);
        assertThat(captureBody()).doesNotContain("Description");
    }

    @Test
    void create_bodyContainsTestPlanIdWhenProvided() {
        givenApiReturns(CASE_RESPONSE);
        service.createTestCase("New Case", 42, null, 99);
        assertThat(captureBody()).contains("\"TestPlan\":{\"Id\":99}");
    }

    @Test
    void create_bodyOmitsTestPlanWhenNull() {
        givenApiReturns(CASE_RESPONSE);
        service.createTestCase("New Case", 42, null, null);
        assertThat(captureBody()).doesNotContain("TestPlan");
    }

    @Test
    void create_mapsResponseCorrectly() {
        givenApiReturns(CASE_RESPONSE);
        TestCaseDto result = service.createTestCase("New Case", 42, null, null);
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("New Case");
        assertThat(result.ownerLogin()).isEqualTo("owner@test.com");
        assertThat(result.createdAt()).isEqualTo("2025-01-15");
    }

    @Test
    void create_apiError_throwsTargetProcessApiException() {
        when(httpClient.post(any(), any())).thenThrow(new TargetProcessApiException(400, "Bad Request"));
        assertThatThrownBy(() -> service.createTestCase("Bad", 0, null, null))
                .isInstanceOf(TargetProcessApiException.class);
    }

    private void givenApiReturns(String body) {
        when(httpClient.post(any(), any())).thenReturn(body);
        when(httpClient.parseSingle(eq(body), eq(TestCase.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(body, TestCase.class));
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






