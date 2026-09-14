package com.ibm.mcp.zdtp.attachment.control;

import com.ibm.mcp.zdtp.attachment.entity.Attachment;
import com.ibm.mcp.zdtp.attachment.entity.AttachmentDto;

public class AttachmentConverter {
    public static Attachment toAttachment(AttachmentDto dto) {
        if (dto == null) return null;
        return new Attachment(
                dto.id(),
                dto.name(),
                dto.description(),
                dto.createDate(),
                dto.owner() != null ? dto.owner().id() : null,
                dto.owner() != null ? dto.owner().name() : null,
                dto.general() != null ? dto.general().id() : null,
                dto.general() != null ? dto.general().name() : null
        );
    }
}
