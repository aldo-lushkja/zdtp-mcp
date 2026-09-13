package com.ibm.mcp.zdtp.task.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.task.entity.TaskDto;
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
class TaskSearchServiceTest {

    private static final String RESPONSE = """
            {"Items":[{"Id":30,"Name":"Task 30"}]}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    TaskSearchService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties("https://tp.example.com", "token");
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new TaskSearchService(engine, new TaskConverter());
    }

    @Test
    void search_returnsTasks() {
        when(httpClient.fetch(any())).thenReturn(RESPONSE);
        when(httpClient.parse(eq(RESPONSE), any())).thenCallRealMethod();

        List<TaskDto> res = service.search(TaskSearchService.SearchCriteria.builder().nameQuery("Dev").build());

        assertThat(res).hasSize(1);
        assertThat(res.get(0).id()).isEqualTo(30);
    }
}
