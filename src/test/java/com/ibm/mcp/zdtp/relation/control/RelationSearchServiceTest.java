package com.ibm.mcp.zdtp.relation.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.relation.entity.RelationDto;
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
class RelationSearchServiceTest {

    private static final String RESPONSE = """
            {"Items":[{"Id":99,"RelationType":{"Name":"Blocker"}}]}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    RelationSearchService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties("https://tp.example.com", "token");
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new RelationSearchService(engine, new RelationConverter());
    }

    @Test
    void findByEntity_returnsRelations() {
        when(httpClient.fetch(any())).thenReturn(RESPONSE);
        when(httpClient.parse(eq(RESPONSE), any())).thenCallRealMethod();

        List<RelationDto> res = service.findByEntity(100);

        assertThat(res).hasSize(1);
        assertThat(res.get(0).typeName()).isEqualTo("Blocker");
    }
}
