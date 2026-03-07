package com.ibm.mcp.targetprocess.feature.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.feature.converter.FeatureConverter;
import com.ibm.mcp.targetprocess.feature.dto.FeatureDto;
import com.ibm.mcp.targetprocess.feature.model.Feature;
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
class FeatureCreateServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    // 1736899200000 ms epoch = 2025-01-15T00:00:00Z
    private static final String FEATURE_RESPONSE = """
            {"Id":200,"Name":"New Feature","Project":{"Id":42,"Name":"satispay_plus"},
            "EntityState":{"Id":10,"Name":"Open"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "EndDate":null,"Effort":8.0,
            "Owner":{"Id":5,"Login":"aldo.lushkja@satispay.com"}}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    FeatureCreateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        service = new FeatureCreateService(props, httpClient, new FeatureConverter(), new ObjectMapper());
    }

    // ── URL tests ────────────────────────────────────────────────────────────────

    @Test
    void create_urlPointsToFeaturesEndpoint() {
        givenApiReturns(FEATURE_RESPONSE);

        service.createFeature("New Feature", 42, null, null);

        assertThat(captureUrl()).contains(BASE_URL + "/api/v1/Features");
    }

    @Test
    void create_urlContainsFormatJson() {
        givenApiReturns(FEATURE_RESPONSE);

        service.createFeature("New Feature", 42, null, null);

        assertThat(captureUrl()).contains("format=json");
    }

    @Test
    void create_urlContainsInclude() {
        givenApiReturns(FEATURE_RESPONSE);

        service.createFeature("New Feature", 42, null, null);

        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8))
                .contains("Owner");
    }

    // ── Request body tests ───────────────────────────────────────────────────────

    @Test
    void create_bodyContainsNameAndProjectId() {
        givenApiReturns(FEATURE_RESPONSE);

        service.createFeature("New Feature", 42, null, null);

        String body = captureBody();
        assertThat(body).contains("\"Name\":\"New Feature\"");
        assertThat(body).contains("\"Id\":42");
    }

    @Test
    void create_bodyContainsDescriptionWhenProvided() {
        givenApiReturns(FEATURE_RESPONSE);

        service.createFeature("New Feature", 42, "Feature description", null);

        assertThat(captureBody()).contains("\"Description\":\"Feature description\"");
    }

    @Test
    void create_bodyOmitsDescriptionWhenBlank() {
        givenApiReturns(FEATURE_RESPONSE);

        service.createFeature("New Feature", 42, "", null);

        assertThat(captureBody()).doesNotContain("Description");
    }

    @Test
    void create_bodyContainsEffortWhenProvided() {
        givenApiReturns(FEATURE_RESPONSE);

        service.createFeature("New Feature", 42, null, 13.0);

        assertThat(captureBody()).contains("\"Effort\":13.0");
    }

    @Test
    void create_bodyOmitsEffortWhenNull() {
        givenApiReturns(FEATURE_RESPONSE);

        service.createFeature("New Feature", 42, null, null);

        assertThat(captureBody()).doesNotContain("Effort");
    }

    // ── Response parsing tests ───────────────────────────────────────────────────

    @Test
    void create_mapsResponseCorrectly() {
        givenApiReturns(FEATURE_RESPONSE);

        FeatureDto result = service.createFeature("New Feature", 42, null, null);

        assertThat(result.id()).isEqualTo(200);
        assertThat(result.name()).isEqualTo("New Feature");
        assertThat(result.projectName()).isEqualTo("satispay_plus");
        assertThat(result.state()).isEqualTo("Open");
        assertThat(result.ownerLogin()).isEqualTo("aldo.lushkja@satispay.com");
        assertThat(result.effort()).isEqualTo(8.0);
        assertThat(result.createdAt()).isEqualTo("2025-01-15");
    }

    @Test
    void create_apiError_throwsTargetProcessApiException() {
        when(httpClient.post(any(), any()))
                .thenThrow(new TargetProcessApiException(400, "{\"Message\":\"Bad Request\"}"));

        assertThatThrownBy(() -> service.createFeature("Bad", 0, null, null))
                .isInstanceOf(TargetProcessApiException.class);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    private void givenApiReturns(String body) {
        when(httpClient.post(any(), any())).thenReturn(body);
        when(httpClient.parseSingle(eq(body), eq(Feature.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(body.trim(), Feature.class));
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