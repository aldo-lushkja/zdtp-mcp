package com.ibm.mcp.zdtp.request.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.control.TargetProcessApiException;
import com.ibm.mcp.zdtp.request.entity.RequestDto;
import com.ibm.mcp.zdtp.request.entity.Request;
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
class RequestCreateServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    // 1736899200000 ms epoch = 2025-01-15T00:00:00Z
    private static final String REQUEST_RESPONSE = """
            {"Id":1,"Name":"New Request","Project":{"Id":42,"Name":"P1"},
            "EntityState":{"Id":2,"Name":"Open"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "Owner":{"Id":5,"Login":"owner@test.com"}}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    RequestCreateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        service = new RequestCreateService(props, httpClient, new RequestConverter(), new ObjectMapper());
    }

    @Test
    void create_urlPointsToRequestsEndpoint() {
        givenApiReturns(REQUEST_RESPONSE);
        service.createRequest("New Request", 42, null, null);
        assertThat(captureUrl()).contains("/api/v1/Requests");
    }

    @Test
    void create_urlContainsFormatJson() {
        givenApiReturns(REQUEST_RESPONSE);
        service.createRequest("New Request", 42, null, null);
        assertThat(captureUrl()).contains("format=json");
    }

    @Test
    void create_urlContainsInclude() {
        givenApiReturns(REQUEST_RESPONSE);
        service.createRequest("New Request", 42, null, null);
        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8)).contains("Owner");
    }

    @Test
    void create_bodyContainsNameAndProjectId() {
        givenApiReturns(REQUEST_RESPONSE);
        service.createRequest("New Request", 42, null, null);
        String body = captureBody();
        assertThat(body).contains("\"Name\":\"New Request\"");
        assertThat(body).contains("\"Id\":42");
    }

    @Test
    void create_bodyContainsDescriptionWhenProvided() {
        givenApiReturns(REQUEST_RESPONSE);
        service.createRequest("New Request", 42, "Request description", null);
        assertThat(captureBody()).contains("\"Description\":\"Request description\"");
    }

    @Test
    void create_bodyOmitsDescriptionWhenBlank() {
        givenApiReturns(REQUEST_RESPONSE);
        service.createRequest("New Request", 42, "", null);
        assertThat(captureBody()).doesNotContain("Description");
    }

    @Test
    void create_bodyOmitsDescriptionWhenNull() {
        givenApiReturns(REQUEST_RESPONSE);
        service.createRequest("New Request", 42, null, null);
        assertThat(captureBody()).doesNotContain("Description");
    }

    @Test
    void create_bodyContainsEffortWhenProvided() {
        givenApiReturns(REQUEST_RESPONSE);
        service.createRequest("New Request", 42, null, 13.0);
        assertThat(captureBody()).contains("\"Effort\":13.0");
    }

    @Test
    void create_mapsResponseCorrectly() {
        givenApiReturns(REQUEST_RESPONSE);
        RequestDto result = service.createRequest("New Request", 42, null, null);
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("New Request");
        assertThat(result.ownerLogin()).isEqualTo("owner@test.com");
        assertThat(result.createdAt()).isEqualTo("2025-01-15");
    }

    @Test
    void create_apiError_throwsTargetProcessApiException() {
        when(httpClient.post(any(), any())).thenThrow(new TargetProcessApiException(400, "Bad Request"));
        assertThatThrownBy(() -> service.createRequest("Bad", 0, null, null))
                .isInstanceOf(TargetProcessApiException.class);
    }

    private void givenApiReturns(String body) {
        when(httpClient.post(any(), any())).thenReturn(body);
        when(httpClient.parseSingle(eq(body), eq(Request.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(body, Request.class));
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
