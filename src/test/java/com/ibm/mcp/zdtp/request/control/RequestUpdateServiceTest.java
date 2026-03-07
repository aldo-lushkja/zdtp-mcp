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
class RequestUpdateServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    private static final String REQUEST_RESPONSE = """
            {"Id":1,"Name":"Updated Request","Project":{"Id":42,"Name":"P1"},
            "EntityState":{"Id":2,"Name":"Open"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "Owner":{"Id":5,"Login":"owner@test.com"}}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    RequestUpdateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        service = new RequestUpdateService(props, httpClient, new RequestConverter(), new ObjectMapper());
    }

    @Test
    void update_urlContainsRequestId() {
        givenApiReturns(REQUEST_RESPONSE);
        service.updateRequest(1, "New Name", null, null, null);
        assertThat(captureUrl()).contains("/api/v1/Requests/1");
    }

    @Test
    void update_urlContainsFormatJson() {
        givenApiReturns(REQUEST_RESPONSE);
        service.updateRequest(1, "New Name", null, null, null);
        assertThat(captureUrl()).contains("format=json");
    }

    @Test
    void update_urlContainsInclude() {
        givenApiReturns(REQUEST_RESPONSE);
        service.updateRequest(1, "New Name", null, null, null);
        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8)).contains("Owner");
    }

    @Test
    void update_bodyContainsNameWhenProvided() {
        givenApiReturns(REQUEST_RESPONSE);
        service.updateRequest(1, "New Name", null, null, null);
        assertThat(captureBody()).contains("\"Name\":\"New Name\"");
    }

    @Test
    void update_bodyOmitsNameWhenBlank() {
        givenApiReturns(REQUEST_RESPONSE);
        service.updateRequest(1, "", "Desc", null, null);
        assertThat(captureBody()).doesNotContain("\"Name\":");
    }

    @Test
    void update_bodyContainsDescriptionWhenProvided() {
        givenApiReturns(REQUEST_RESPONSE);
        service.updateRequest(1, null, "New Description", null, null);
        assertThat(captureBody()).contains("\"Description\":\"New Description\"");
    }

    @Test
    void update_bodyContainsEntityStateWhenProvided() {
        givenApiReturns(REQUEST_RESPONSE);
        service.updateRequest(1, null, null, "Open", null);
        assertThat(captureBody()).contains("\"EntityState\":{\"Name\":\"Open\"}");
    }

    @Test
    void update_bodyOmitsEntityStateWhenBlank() {
        givenApiReturns(REQUEST_RESPONSE);
        service.updateRequest(1, null, null, "", null);
        assertThat(captureBody()).doesNotContain("EntityState");
    }

    @Test
    void update_bodyContainsEffortWhenProvided() {
        givenApiReturns(REQUEST_RESPONSE);
        service.updateRequest(1, null, null, null, 13.0);
        assertThat(captureBody()).contains("\"Effort\":13.0");
    }

    @Test
    void update_emptyBody_whenAllFieldsNull() {
        givenApiReturns(REQUEST_RESPONSE);
        service.updateRequest(1, null, null, null, null);
        assertThat(captureBody()).isEqualTo("{}");
    }

    @Test
    void update_mapsResponseCorrectly() {
        givenApiReturns(REQUEST_RESPONSE);
        RequestDto result = service.updateRequest(1, "Updated Request", null, null, null);
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("Updated Request");
    }

    @Test
    void update_apiError_throwsTargetProcessApiException() {
        when(httpClient.post(any(), any())).thenThrow(new TargetProcessApiException(400, "Bad Request"));
        assertThatThrownBy(() -> service.updateRequest(1, "Name", null, null, null))
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
