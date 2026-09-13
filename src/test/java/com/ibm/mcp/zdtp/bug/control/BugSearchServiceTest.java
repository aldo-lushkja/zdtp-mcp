package com.ibm.mcp.zdtp.bug.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.bug.entity.BugDto;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BugSearchServiceTest {

    private static final String BUGS_RESPONSE = """
            {"Items":[{"Id":101,"Name":"Bug 101"},{"Id":102,"Name":"Bug 102"}]}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    BugSearchService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties("https://tp.example.com", "token");
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new BugSearchService(engine, new BugConverter());
    }

    @Test
    void search_returnsMappedDtos() {
        when(httpClient.fetch(any())).thenReturn(BUGS_RESPONSE);
        when(httpClient.parse(eq(BUGS_RESPONSE), any())).thenCallRealMethod();

        List<BugDto> results = service.search(BugSearchService.SearchCriteria.builder().nameQuery("Crash").build());

        assertThat(results).hasSize(2);
        assertThat(results.get(0).id()).isEqualTo(101);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(httpClient).fetch(captor.capture());
        assertThat(URLDecoder.decode(captor.getValue(), StandardCharsets.UTF_8)).contains("Name contains 'Crash'");
    }
}
