package com.ibm.mcp.zdtp.time.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.time.entity.TimeEntry;
import com.ibm.mcp.zdtp.time.entity.TimeEntryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimeLogServiceTest {

    private static final String RESPONSE = """
            {"Id":77,"Spent":2.5,"Description":"Dev work"}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    TimeLogService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties("https://tp.example.com", "token");
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new TimeLogService(engine, new TimeConverter());
    }

    @Test
    void logTime_createsTimeRecord() {
        when(httpClient.post(any(), any())).thenReturn(RESPONSE);
        when(httpClient.parseSingle(eq(RESPONSE), eq(TimeEntry.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(RESPONSE, TimeEntry.class));

        TimeEntryDto dto = service.logTime(10, 2.5, "Dev work");

        assertThat(dto.id()).isEqualTo(77);
        assertThat(dto.spent()).isEqualTo(2.5);
        verify(httpClient).post(any(), any());
    }
}
