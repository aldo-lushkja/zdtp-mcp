package com.ibm.mcp.zdtp.attachment.control;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.attachment.entity.Attachment;
import com.ibm.mcp.zdtp.attachment.entity.AttachmentDto;
import com.ibm.mcp.zdtp.shared.model.TargetProcessResponse;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttachmentSearchService {
    private final QueryEngine engine;

    public AttachmentSearchService(QueryEngine engine) {
        this.engine = engine;
    }

    public List<Attachment> search(int entityId, Integer take) {
        Map<String, String> params = new HashMap<>();
        params.put("where", "General.Id eq " + entityId);
        if (take != null && take > 0) {
            params.put("take", String.valueOf(take));
        }

        return engine.list(
                QueryEngine.ATTACHMENT,
                params,
                new TypeReference<TargetProcessResponse<AttachmentDto>>() {},
                AttachmentConverter::toAttachment
        );
    }
}
