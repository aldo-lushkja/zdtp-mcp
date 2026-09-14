package com.ibm.mcp.zdtp.attachment.entity;

import java.time.OffsetDateTime;

public record Attachment(
        Integer id,
        String name,
        String description,
        OffsetDateTime createDate,
        Integer ownerId,
        String ownerLogin,
        Integer entityId,
        String entityName
) {}
