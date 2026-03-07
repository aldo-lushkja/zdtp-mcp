package com.ibm.mcp.zdtp.request.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.request.control.RequestConverter;
import com.ibm.mcp.zdtp.request.entity.RequestDto;
import com.ibm.mcp.zdtp.request.entity.Request;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.control.TargetProcessApiException;
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
    // 1736899200000 ms epoch = 2025-01-15T00:00:00Z
    private static final String REQUEST_RESPONSE = """
            {"Id":99,"Name":"Updated Request","Project":{"Id":1,"Name":"satispay_plus"},
            "EntityState":{"Id":11,"Name":"In Progress"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "EndDate":null,"Effort":21.0,
            "Owner":{"Id":5,"Login":"aldo.lushkja@satispay.com"}}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    RequestUpdateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        service = new RequestUpdateService(props, httpClient, new RequestConverter(), new ObjectMapper());
    }

    // ── URL tests ────────────────────────────────────────────────────────────────

    @Test
    void update_urlContainsRequestId() {
        givenApiReturns(REQUEST_RESPONSE);

        service.updateRequest(99, null, null, null, null);

        assertThat(captureUrl()).contains("/api/v1/Requests/99");
    }

    @Test
    void update_urlContainsFormatJson() {
        givenApiReturns(REQUEST_RESPONSE);

        service.updateRequest(99, null, null, null, null);

        assertThat(captureUrl()).contains("format=json");
    }

    @Test
    void update_urlContainsInclude() {
        givenApiReturns(REQUEST_RESPONSE);

        service.updateRequest(99, null, null, null, null);

        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8))
                .contains("Owner");
    }

    // ── Request body tests ───────────────────────────────────────────────────────

    @Test
    void update_bodyContainsNameWhenProvided() {
        givenApiReturns(REQUEST_RESPONSE);

        service.updateRequest(99, "Updated Request", null, null, null);

        assertThat(captureBody()).contains("\"Name\":\"Updated Request\"");
    }

    @Test
    void update_bodyOmitsNameWhenBlank() {
        givenApiReturns(REQUEST_RESPONSE);

        service.updateRequest(99, "", null, null, null);

        assertThat(captureBody()).doesNotContain("Name");
    }

    @Test
    void update_bodyContainsDescriptionWhenProvided() {
        givenApiReturns(REQUEST_RESPONSE);

        service.updateRequest(99, null, "New description", null, null);

        assertThat(captureBody()).contains("\"Description\":\"New description\"");
    }

    @Test
    void update_bodyContainsEntityStateWhenProvided() {
        givenApiReturns(REQUEST_RESPONSE);

        service.updateRequest(99, null, null, "In Progress", null);

        String body = captureBody();
        assertThat(body).contains("EntityState");
        assertThat(body).contains("\"Name\":\"In Progress\"");
    }

    @Test
    void update_bodyOmitsEntityStateWhenBlank() {
        givenApiReturns(REQUEST_RESPONSE);

        service.updateRequest(99, null, null, "", null);

        assertThat(captureBody()).doesNotContain("EntityState");
    }

    @Test
    void update_bodyContainsEffortWhenProvided() {
        givenApiReturns(REQUEST_RESPONSE);

        service.updateRequest(99, null, null, null, 21.0);

        assertThat(captureBody()).contains("\"Effort\":21.0");
    }

    @Test
    void update_emptyBody_whenAllFieldsNull() {
        givenApiReturns(REQUEST_RESPONSE);

        service.updateRequest(99, null, null, null, null);

        assertThat(captureBody()).isEqualTo("{}");
    }

    // ── Response parsing tests ───────────────────────────────────────────────────

    @Test
    void update_mapsResponseCorrectly() {
        givenApiReturns(REQUEST_RESPONSE);

        RequestDto result = service.updateRequest(99, "Updated Request", null, "In Progress", 21.0);

        assertThat(result.id()).isEqualTo(99);
        assertThat(result.name()).isEqualTo("Updated Request");
        assertThat(result.state()).isEqualTo("In Progress");
        assertThat(result.effort()).isEqualTo(21.0);
        assertThat(result.createdAt()).isEqualTo("2025-01-15");
    }

    @Test
    void update_apiError_throwsTargetProcessApiException() {
        when(httpClient.post(any(), any()))
                .thenThrow(new TargetProcessApiException(404, "{\"Message\":\"Not Found\"}"));

        assertThatThrownBy(() -> service.updateRequest(999, "X", null, null, null))
                .isInstanceOf(TargetProcessApiException.class);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    private void givenApiReturns(String body) {
        when(httpClient.post(any(), any())).thenReturn(body);
        when(httpClient.parseSingle(eq(body), eq(Request.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(body.trim(), Request.class));
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
