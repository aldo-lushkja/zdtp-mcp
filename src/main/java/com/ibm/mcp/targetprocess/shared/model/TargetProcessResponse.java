package com.ibm.mcp.targetprocess.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record TargetProcessResponse<T>(
    @JsonProperty("Items") List<T> items,
    @JsonProperty("Next") String next
) {}
