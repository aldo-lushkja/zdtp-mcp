package com.ibm.mcp.zdtp.time.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.mcp.zdtp.shared.entity.Owner;

public record TimeEntry(
    @JsonProperty("Id") Integer id,
    @JsonProperty("Spent") Double spent,
    @JsonProperty("Description") String description,
    @JsonProperty("Date") String date,
    @JsonProperty("User") Owner user
) {}
