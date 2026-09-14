package com.ibm.mcp.zdtp.attachment.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.mcp.zdtp.shared.entity.NamedEntity;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AttachmentDto(
        @JsonProperty("Id") Integer id,
        @JsonProperty("Name") String name,
        @JsonProperty("Description") String description,
        @JsonProperty("CreateDate") OffsetDateTime createDate,
        @JsonProperty("Owner") NamedEntity owner,
        @JsonProperty("General") NamedEntity general
) {}
