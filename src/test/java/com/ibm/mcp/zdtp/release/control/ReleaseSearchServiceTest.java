package com.ibm.mcp.zdtp.release.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.release.entity.ReleaseDto;
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
class ReleaseSearchServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    private static final String RELEASES_RESPONSE = """
            {"Items":[{"Id":1,"Name":"Rel 1"},{"Id":2,"Name":"Rel 2"}]}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    ReleaseSearchService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        service = new ReleaseSearchService(props, httpClient, new ReleaseConverter(), new ObjectMapper());
    }

    @Test
    void search_returnsMappedReleases() {
        when(httpClient.fetch(any())).thenReturn(RELEASES_RESPONSE);
        when(httpClient.parse(any(), any())).thenCallRealMethod();

        List<ReleaseDto> result = service.searchReleases(null, null, null, null, null, 10, null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Rel 1");
    }

    @Test
    void search_buildsCorrectUrl() {
        when(httpClient.fetch(any())).thenReturn(RELEASES_RESPONSE);
        when(httpClient.parse(any(), any())).thenCallRealMethod();

        service.searchReleases("test", "P1", "owner", "2024-01-01", null, 5, null);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpClient).fetch(urlCaptor.capture());
        String url = urlCaptor.getValue();

        assertThat(url).contains("/api/v1/Releases");
        assertThat(url).contains("where=" + httpClient.encode("Name contains 'test' and Project.Name contains 'P1' and Owner.Login eq 'owner' and StartDate gte '2024-01-01'"));
        assertThat(url).contains("take=5");
    }
}
