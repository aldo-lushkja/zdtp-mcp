package com.ibm.mcp.zdtp.shared.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.entity.TargetProcessResponse;

public abstract class BaseService {

    protected final TargetProcessProperties properties;
    protected final TargetProcessHttpClient httpClient;

    protected BaseService(TargetProcessProperties properties, TargetProcessHttpClient httpClient) {
        this.properties = properties;
        this.httpClient = httpClient;
    }

    protected <T, D> List<D> fetchList(String resource, Map<String, String> params, TypeReference<TargetProcessResponse<T>> typeRef, Function<T, D> mapper) {
        String body = httpClient.fetch(buildUrl(resource, params));
        TargetProcessResponse<T> resp = httpClient.parse(body, typeRef);
        return Optional.ofNullable(resp.items()).orElse(List.of()).stream().map(mapper).toList();
    }

    protected <T, D> D fetchSingle(String resource, Map<String, String> params, Class<T> clazz, Function<T, D> mapper) {
        String body = httpClient.fetch(buildUrl(resource, params));
        return mapper.apply(httpClient.parseSingle(body, clazz));
    }

    protected <T, D> D postSingle(String resource, Map<String, String> params, String body, Class<T> clazz, Function<T, D> mapper) {
        String response = httpClient.post(buildUrl(resource, params), body);
        return mapper.apply(httpClient.parseSingle(response, clazz));
    }

    protected String buildUrl(String resourcePath, Map<String, String> queryParams) {
        // Use a TreeMap to maintain consistent order for test verification
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
                if (value instanceof String) {
                    conditions.add("%s %s '%s'".formatted(field, operator, value));
                } else {
                    conditions.add("%s %s %s".formatted(field, operator, value));
                }
            }
            return this;
        }

        public ODataQueryBuilder add(String condition) {
            if (condition != null && !condition.isBlank()) {
                conditions.add(condition);
            }
            return this;
        }

        public String build() {
            return String.join(" and ", conditions);
        }
    }
}
