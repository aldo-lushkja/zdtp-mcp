package com.ibm.mcp.zdtp.customfield.control;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ibm.mcp.zdtp.customfield.entity.CustomFieldInfo;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomFieldListServiceTest {

    @Mock TargetProcessHttpClient httpClient;

    @Test
    void list_returnsCustomFields() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        QueryEngine engine = new QueryEngine(new TargetProcessProperties("https://test.tpondemand.com", "token"), httpClient, mapper);
        CustomFieldListService service = new CustomFieldListService(engine);

        when(httpClient.fetch(anyString())).thenReturn("{\"Items\":[{\"Id\":1,\"Name\":\"Severity\",\"FieldType\":\"DropDown\",\"EntityKind\":\"Bug\"}]}");
        when(httpClient.parse(anyString(), any(TypeReference.class))).thenCallRealMethod();

        List<CustomFieldInfo> fields = service.list("Bug", null, 10);

        assertThat(fields).hasSize(1);
        assertThat(fields.get(0).name()).isEqualTo("Severity");
        assertThat(fields.get(0).entityKind()).isEqualTo("Bug");
    }
}
