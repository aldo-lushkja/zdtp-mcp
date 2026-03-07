package com.ibm.mcp.zdtp.feature.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.feature.entity.FeatureDto;
import com.ibm.mcp.zdtp.feature.entity.Feature;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeatureSearchServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    private static final String FEATURES_RESPONSE = """
            {"Items":[{"Id":1,"Name":"Feature 1"},{"Id":2,"Name":"Feature 2"}]}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    FeatureSearchService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        service = new FeatureSearchService(props, httpClient, new FeatureConverter(), new ObjectMapper());
    }

    @Test
    void search_returnsMappedFeatures() {
        when(httpClient.fetch(any())).thenReturn(FEATURES_RESPONSE);
        when(httpClient.parse(any(), any())).thenCallRealMethod();

        List<FeatureDto> result = service.searchFeatures(null, null, null, null, null, 10, null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Feature 1");
    }

    @Test
    void search_buildsCorrectUrl() {
        when(httpClient.fetch(any())).thenReturn(FEATURES_RESPONSE);
        when(httpClient.parse(any(), any())).thenCallRealMethod();

        service.searchFeatures("test", "P1", "owner", "2024-01-01", null, 5, null);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpClient).fetch(urlCaptor.capture());
        String url = urlCaptor.getValue();

        assertThat(url).contains("/api/v1/Features");
        assertThat(url).contains("where=" + httpClient.encode("Name contains 'test' and Project.Name contains 'P1' and Owner.Login eq 'owner' and CreateDate gte '2024-01-01'"));
        assertThat(url).contains("take=5");
    }
}
