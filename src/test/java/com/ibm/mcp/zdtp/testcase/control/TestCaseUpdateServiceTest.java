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
class TestCaseUpdateServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    private static final String CASE_RESPONSE = """
            {"Id":1,"Name":"Updated Case","Project":{"Id":42,"Name":"P1"},
            "EntityState":{"Id":2,"Name":"Draft"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "Owner":{"Id":5,"Login":"owner@test.com"}}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    TestCaseUpdateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new TestCaseUpdateService(engine, new TestCaseConverter());
    }

    @Test
    void update_urlContainsTestCaseId() {
        givenApiReturns(CASE_RESPONSE);
        service.updateTestCase(1, "New Name", null, null);
        assertThat(captureUrl()).contains("/api/v1/TestCases/1");
    }

    @Test
    void update_urlContainsFormatJson() {
        givenApiReturns(CASE_RESPONSE);
        service.updateTestCase(1, "New Name", null, null);
        assertThat(captureUrl()).contains("format=json");
    }

    @Test
    void update_urlContainsInclude() {
        givenApiReturns(CASE_RESPONSE);
        service.updateTestCase(1, "New Name", null, null);
        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8)).contains("Owner");
    }

    @Test
    void update_bodyContainsNameWhenProvided() {
        givenApiReturns(CASE_RESPONSE);
        service.updateTestCase(1, "New Name", null, null);
        assertThat(captureBody()).contains("\"Name\":\"New Name\"");
    }

    @Test
    void update_bodyOmitsNameWhenBlank() {
        givenApiReturns(CASE_RESPONSE);
        service.updateTestCase(1, "", "Desc", null);
        assertThat(captureBody()).doesNotContain("\"Name\":");
    }

    @Test
    void update_bodyContainsDescriptionWhenProvided() {
        givenApiReturns(CASE_RESPONSE);
        service.updateTestCase(1, null, "New Description", null);
        assertThat(captureBody()).contains("\"Description\":\"New Description\"");
    }

    @Test
    void update_bodyContainsEntityStateWhenProvided() {
        givenApiReturns(CASE_RESPONSE);
        service.updateTestCase(1, null, null, "Draft");
        assertThat(captureBody()).contains("\"EntityState\":{\"Name\":\"Draft\"}");
    }

    @Test
    void update_bodyOmitsEntityStateWhenBlank() {
        givenApiReturns(CASE_RESPONSE);
        service.updateTestCase(1, null, null, "");
        assertThat(captureBody()).doesNotContain("EntityState");
    }

    @Test
    void update_emptyBody_whenAllFieldsNull() {
        givenApiReturns(CASE_RESPONSE);
        service.updateTestCase(1, null, null, null);
        assertThat(captureBody()).isEqualTo("{}");
    }

    @Test
    void update_mapsResponseCorrectly() {
        givenApiReturns(CASE_RESPONSE);
        TestCaseDto result = service.updateTestCase(1, "Updated Case", null, null);
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("Updated Case");
    }

    @Test
    void update_apiError_throwsTargetProcessApiException() {
        when(httpClient.post(any(), any())).thenThrow(new TargetProcessApiException(400, "Bad Request"));
        assertThatThrownBy(() -> service.updateTestCase(1, "Name", null, null))
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






