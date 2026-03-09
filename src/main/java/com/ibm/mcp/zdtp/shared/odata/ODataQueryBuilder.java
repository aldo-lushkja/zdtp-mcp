package com.ibm.mcp.zdtp.shared.odata;

import java.util.ArrayList;
import java.util.List;

public class ODataQueryBuilder {
    private final List<String> conditions = new ArrayList<>();

    public ODataQueryBuilder add(String field, String operator, Object value) {
        if (value != null && (!(value instanceof String s) || !s.isBlank())) {
            conditions.add(value instanceof String ? "%s %s '%s'".formatted(field, operator, value) : "%s %s %s".formatted(field, operator, value));
        }
        return this;
    }

    public ODataQueryBuilder add(String condition) {
        if (condition != null && !condition.isBlank()) conditions.add(condition);
        return this;
    }

    public String build() {
        return String.join(" and ", conditions);
    }
}
