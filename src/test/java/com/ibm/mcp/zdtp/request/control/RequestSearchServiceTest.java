package com.ibm.mcp.zdtp.request.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.request.entity.RequestDto;
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
class RequestSearchServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    private static final String REQUESTS_RESPONSE = """
            {"Items":[{"Id":1,"Name":"Req 1"},{"Id":2,"Name":"Req 2"}]}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    RequestSearchService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        service = new RequestSearchService(props, httpClient, new RequestConverter(), new ObjectMapper());
    }

    @Test
    void search_returnsMappedRequests() {
        when(httpClient.fetch(any())).thenReturn(REQUESTS_RESPONSE);
        when(httpClient.parse(any(), any())).thenCallRealMethod();

        List<RequestDto> result = service.searchRequests(null, null, null, null, null, 10);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Req 1");
    }

    @Test
    void search_buildsCorrectUrl() {
        when(httpClient.fetch(any())).thenReturn(REQUESTS_RESPONSE);
        when(httpClient.parse(any(), any())).thenCallRealMethod();

        service.searchRequests("test", "P1", "owner", null, null, 5);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpClient).fetch(urlCaptor.capture());
        String url = urlCaptor.getValue();

        assertThat(url).contains("/api/v1/Requests");
        assertThat(url).contains("where=" + httpClient.encode("Name contains 'test' and Project.Name contains 'P1' and Owner.Login eq 'owner'"));
        assertThat(url).contains("take=5");
    }
}
