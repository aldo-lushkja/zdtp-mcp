package com.ibm.mcp.targetprocess.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record TargetProcessResponse<T>(
    @JsonProperty("items") List<T> items,
    @JsonProperty("next") String next
) {}
