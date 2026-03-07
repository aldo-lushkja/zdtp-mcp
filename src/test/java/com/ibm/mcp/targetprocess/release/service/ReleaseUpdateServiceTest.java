package com.ibm.mcp.targetprocess.release.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.release.converter.ReleaseConverter;
import com.ibm.mcp.targetprocess.release.dto.ReleaseDto;
import com.ibm.mcp.targetprocess.release.model.Release;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.shared.exception.TargetProcessApiException;
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
class ReleaseUpdateServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    // 1736899200000 ms epoch = 2025-01-15T00:00:00Z
    private static final String RELEASE_RESPONSE = """
            {"Id":77,"Name":"Updated Release","Project":{"Id":1,"Name":"satispay_plus"},
            "EntityState":{"Id":11,"Name":"In Progress"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "StartDate":null,"EndDate":null,"Effort":8.0,
            "Owner":{"Id":5,"Login":"aldo.lushkja@satispay.com"}}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    ReleaseUpdateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        service = new ReleaseUpdateService(props, httpClient, new ReleaseConverter(), new ObjectMapper());
    }

    // ── URL tests ────────────────────────────────────────────────────────────────

    @Test
    void update_urlContainsReleaseId() {
        givenApiReturns(RELEASE_RESPONSE);

        service.updateRelease(77, null, null, null, null);

        assertThat(captureUrl()).contains("/api/v1/Releases/77");
    }

    @Test
    void update_urlContainsFormatJson() {
        givenApiReturns(RELEASE_RESPONSE);

        service.updateRelease(77, null, null, null, null);

        assertThat(captureUrl()).contains("format=json");
    }

    @Test
    void update_urlContainsIncludeWithStartDate() {
        givenApiReturns(RELEASE_RESPONSE);

        service.updateRelease(77, null, null, null, null);

        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8))
                .contains("StartDate");
    }

    // ── Request body tests ───────────────────────────────────────────────────────

    @Test
    void update_bodyContainsNameWhenProvided() {
        givenApiReturns(RELEASE_RESPONSE);

        service.updateRelease(77, "Updated Release", null, null, null);

        assertThat(captureBody()).contains("\"Name\":\"Updated Release\"");
    }

    @Test
    void update_bodyOmitsNameWhenBlank() {
        givenApiReturns(RELEASE_RESPONSE);

        service.updateRelease(77, "", null, null, null);

        assertThat(captureBody()).doesNotContain("Name");
    }

    @Test
    void update_bodyContainsDescriptionWhenProvided() {
        givenApiReturns(RELEASE_RESPONSE);

        service.updateRelease(77, null, "New description", null, null);

        assertThat(captureBody()).contains("\"Description\":\"New description\"");
    }

    @Test
    void update_bodyContainsEntityStateWhenProvided() {
        givenApiReturns(RELEASE_RESPONSE);

        service.updateRelease(77, null, null, "In Progress", null);

        String body = captureBody();
        assertThat(body).contains("EntityState");
        assertThat(body).contains("\"Name\":\"In Progress\"");
    }

    @Test
    void update_bodyOmitsEntityStateWhenBlank() {
        givenApiReturns(RELEASE_RESPONSE);

        service.updateRelease(77, null, null, "", null);

        assertThat(captureBody()).doesNotContain("EntityState");
    }

    @Test
    void update_bodyContainsEffortWhenProvided() {
        givenApiReturns(RELEASE_RESPONSE);

        service.updateRelease(77, null, null, null, 8.0);

        assertThat(captureBody()).contains("\"Effort\":8.0");
    }

    @Test
    void update_emptyBody_whenAllFieldsNull() {
        givenApiReturns(RELEASE_RESPONSE);

        service.updateRelease(77, null, null, null, null);

        assertThat(captureBody()).isEqualTo("{}");
    }

    // ── Response parsing tests ───────────────────────────────────────────────────

    @Test
    void update_mapsResponseCorrectly() {
        givenApiReturns(RELEASE_RESPONSE);

        ReleaseDto result = service.updateRelease(77, "Updated Release", null, "In Progress", 8.0);

        assertThat(result.id()).isEqualTo(77);
        assertThat(result.name()).isEqualTo("Updated Release");
        assertThat(result.state()).isEqualTo("In Progress");
        assertThat(result.effort()).isEqualTo(8.0);
        assertThat(result.createdAt()).isEqualTo("2025-01-15");
    }

    @Test
    void update_apiError_throwsTargetProcessApiException() {
        when(httpClient.post(any(), any()))
                .thenThrow(new TargetProcessApiException(404, "{\"Message\":\"Not Found\"}"));

        assertThatThrownBy(() -> service.updateRelease(999, "X", null, null, null))
                .isInstanceOf(TargetProcessApiException.class);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    private void givenApiReturns(String body) {
        when(httpClient.post(any(), any())).thenReturn(body);
        when(httpClient.parseSingle(eq(body), eq(Release.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(body.trim(), Release.class));
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
