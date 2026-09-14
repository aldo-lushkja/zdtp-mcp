package com.ibm.mcp.zdtp.tag.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.tag.entity.AssignableTagDto;
import com.ibm.mcp.zdtp.tag.entity.TagResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagAddServiceTest {

    @Mock TargetProcessHttpClient httpClient;

    @Test
    void addTag_appendsTagToExisting() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        QueryEngine engine = new QueryEngine(new TargetProcessProperties("https://test.tpondemand.com", "token"), httpClient, mapper);
        TagAddService service = new TagAddService(engine);

        when(httpClient.fetch(anyString())).thenReturn("{\"Id\":42,\"Tags\":\"frontend\"}");
        when(httpClient.post(anyString(), anyString())).thenReturn("{\"Id\":42,\"Tags\":\"frontend, critical\"}");
        when(httpClient.parseSingle(anyString(), eq(AssignableTagDto.class))).thenCallRealMethod();

        TagResult result = service.addTag(42, "critical");

        assertThat(result.entityId()).isEqualTo(42);
        assertThat(result.tags()).isEqualTo("frontend, critical");
    }
}
