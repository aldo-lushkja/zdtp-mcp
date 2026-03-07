package com.ibm.mcp.zdtp.shared.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record TargetProcessItems<T>(
    @JsonProperty("Items") List<T> items
) {}
