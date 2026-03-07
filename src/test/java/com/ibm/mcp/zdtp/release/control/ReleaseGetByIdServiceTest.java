package com.ibm.mcp.zdtp.release.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.release.entity.ReleaseDto;
import com.ibm.mcp.zdtp.release.entity.Release;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReleaseGetByIdServiceTest {

    @Mock
    TargetProcessHttpClient httpClient;

    ReleaseGetByIdService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties("http://test", "token");
        service = new ReleaseGetByIdService(props, httpClient, new ReleaseConverter(), new ObjectMapper());
    }

    @Test
    void getById_returnsMappedRelease() {
        String body = "{\"Id\":123,\"Name\":\"Rel A\",\"Project\":{\"Id\":1,\"Name\":\"P\"}}";
        when(httpClient.fetch(any())).thenReturn(body);
        when(httpClient.parseSingle(eq(body), eq(Release.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(body, Release.class));

        ReleaseDto result = service.getById(123);
        assertThat(result.id()).isEqualTo(123);
        assertThat(result.name()).isEqualTo("Rel A");
    }
}
