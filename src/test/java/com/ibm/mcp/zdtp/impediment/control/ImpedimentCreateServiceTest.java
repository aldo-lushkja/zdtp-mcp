package com.ibm.mcp.zdtp.impediment.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.impediment.entity.Impediment;
import com.ibm.mcp.zdtp.impediment.entity.ImpedimentDto;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
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
class ImpedimentCreateServiceTest {

    private static final String RESPONSE = """
            {"Id":15,"Name":"Blocked by API"}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    ImpedimentCreateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties("https://tp.example.com", "token");
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new ImpedimentCreateService(engine, new ImpedimentConverter());
    }

    @Test
    void create_createsImpediment() {
        when(httpClient.post(any(), any())).thenReturn(RESPONSE);
        when(httpClient.parseSingle(eq(RESPONSE), eq(Impediment.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(RESPONSE, Impediment.class));

        ImpedimentDto dto = service.create(100, "Blocked by API", "Details");

        assertThat(dto.id()).isEqualTo(15);
        assertThat(dto.name()).isEqualTo("Blocked by API");
        verify(httpClient).post(any(), any());
    }
}
