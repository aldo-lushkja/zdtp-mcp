package com.ibm.mcp.zdtp.relation.control;

import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RelationDeleteServiceTest {

    private static final String BASE_URL = "http://test.com";
    private static final String TOKEN = "token";

    @Mock
    private TargetProcessHttpClient httpClient;

    private RelationDeleteService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties properties = new TargetProcessProperties(BASE_URL, TOKEN);
        QueryEngine engine = new QueryEngine(properties, httpClient, new ObjectMapper());
        service = new RelationDeleteService(engine);
    }

    @Test
    void delete_callsApiWithCorrectUrl() {
        service.delete(123);
        verify(httpClient).delete(contains("/Relations/123"));
    }
}
