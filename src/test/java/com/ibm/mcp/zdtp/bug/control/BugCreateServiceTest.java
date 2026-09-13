package com.ibm.mcp.zdtp.bug.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.bug.entity.Bug;
import com.ibm.mcp.zdtp.bug.entity.BugDto;
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
class BugCreateServiceTest {

    private static final String CREATE_RESPONSE = """
            {"Id":50,"Name":"New Bug","Description":"Details"}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    BugCreateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties("https://tp.example.com", "token");
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new BugCreateService(engine, new BugConverter());
    }

    @Test
    void create_postsNewBug() {
        when(httpClient.post(any(), any())).thenReturn(CREATE_RESPONSE);
        when(httpClient.parseSingle(eq(CREATE_RESPONSE), eq(Bug.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(CREATE_RESPONSE, Bug.class));

        BugDto result = service.create("New Bug", 1, "Details", 2.0, null, null);

        assertThat(result.id()).isEqualTo(50);
        assertThat(result.name()).isEqualTo("New Bug");
        verify(httpClient).post(any(), any());
    }
}
