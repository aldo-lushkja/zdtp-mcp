package com.ibm.mcp.zdtp.epic.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.epic.entity.EpicDto;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EpicSearchServiceTest {

    private static final String RESPONSE = """
            {"Items":[{"Id":10,"Name":"Epic 10"}]}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    EpicSearchService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties("https://tp.example.com", "token");
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new EpicSearchService(engine, new EpicConverter());
    }

    @Test
    void search_returnsEpics() {
        when(httpClient.fetch(any())).thenReturn(RESPONSE);
        when(httpClient.parse(eq(RESPONSE), any())).thenCallRealMethod();

        List<EpicDto> res = service.search(EpicSearchService.SearchCriteria.builder().nameQuery("Cloud").build());

        assertThat(res).hasSize(1);
        assertThat(res.get(0).name()).isEqualTo("Epic 10");
    }
}
