package com.ibm.mcp.zdtp.shared.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.entity.TargetProcessResponse;

public abstract class BaseService {

    protected final TargetProcessProperties properties;
    protected final TargetProcessHttpClient httpClient;
    protected final QueryEngine engine;

    protected BaseService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, ObjectMapper mapper) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.engine = new QueryEngine(properties, httpClient, mapper);
    }

    protected String buildUrl(String resourcePath, Map<String, String> queryParams) {
        Map<String, String> params = new TreeMap<>(queryParams);
        params.put("format", "json");
        params.put("access_token", properties.accessToken());
        String query = params.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().isBlank())
                .map(e -> e.getKey() + "=" + httpClient.encode(e.getValue()))
                .collect(Collectors.joining("&"));
        return properties.baseUrl() + "/api/v1/" + resourcePath + (query.isEmpty() ? "" : "?" + query);
    }

    protected ODataQueryBuilder query() {
        return new ODataQueryBuilder();
    }

    public static class ODataQueryBuilder {
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

        public String build() { return String.join(" and ", conditions); }
    }
}
