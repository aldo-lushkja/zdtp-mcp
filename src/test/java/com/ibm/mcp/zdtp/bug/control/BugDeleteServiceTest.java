package com.ibm.mcp.zdtp.bug.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BugDeleteServiceTest {

    @Mock
    TargetProcessHttpClient httpClient;

    BugDeleteService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties("https://tp.example.com", "token");
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new BugDeleteService(engine);
    }

    @Test
    void delete_callsDeleteOnApi() {
        service.delete(123);
        verify(httpClient).delete(contains("/Bugs/123"));
    }
}
