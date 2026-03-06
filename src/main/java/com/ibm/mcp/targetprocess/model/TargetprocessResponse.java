package com.ibm.mcp.targetprocess.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record TargetprocessResponse<T>(
    @JsonProperty("Items") List<T> items,
    @JsonProperty("Next") String next
) {}
