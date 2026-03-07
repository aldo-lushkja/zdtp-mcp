package com.ibm.mcp.zdtp.shared.control;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.entity.TargetProcessResponse;

public class QueryEngine {
    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final ObjectMapper mapper;

    public QueryEngine(TargetProcessProperties properties, TargetProcessHttpClient httpClient, ObjectMapper mapper) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    public record Domain(String resource, String include) {}

    public static final Domain USER_STORY = new Domain("UserStories", "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login],AssignedUser[Id,Login],Release[Id,Name],TeamIteration[Id,Name]]");
    public static final Domain FEATURE = new Domain("Features", "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login],TeamIteration[Id,Name]]");
    public static final Domain EPIC = new Domain("Epics", "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login]]");
    public static final Domain RELEASE = new Domain("Releases", "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,StartDate,EndDate,Effort,Owner[Id,Login]]");
    public static final Domain REQUEST = new Domain("Requests", "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login]]");
    public static final Domain TEST_PLAN = new Domain("TestPlans", "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,Owner[Id,Login]]");
    public static final Domain TEST_CASE = new Domain("TestCases", "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,Owner[Id,Login],TestPlan[Id,Name]]");
    public static final Domain TEAM = new Domain("Teams", "[Id,Name]");
    public static final Domain TEAM_ITERATION = new Domain("TeamIterations", "[Id,Name,StartDate,EndDate,Team[Id,Name]]");
    public static final Domain PROJECT = new Domain("Projects", "[Id,Name]");

    public <T, D> List<D> list(Domain domain, Map<String, String> params, TypeReference<TargetProcessResponse<T>> typeRef, Function<T, D> mapper) {
        Map<String, String> p = new TreeMap<>(params);
        if (!domain.include().isBlank()) p.putIfAbsent("include", domain.include());
        var resp = httpClient.parse(httpClient.fetch(url(domain.resource(), p)), typeRef);
        return Optional.ofNullable(resp.items()).orElse(List.of()).stream().map(mapper).toList();
    }

    public <T, D> D get(Domain domain, int id, Function<T, D> mapper, Class<T> clazz) {
        return mapper.apply(httpClient.parseSingle(httpClient.fetch(url(domain.resource() + "/" + id, Map.of("include", domain.include()))), clazz));
    }

    public <T, D> D create(Domain domain, Map<String, Object> body, Function<T, D> mapper, Class<T> clazz) {
        return mapper.apply(httpClient.parseSingle(httpClient.post(url(domain.resource(), Map.of("include", domain.include())), json(body)), clazz));
    }

    public <T, D> D update(Domain domain, int id, Map<String, Object> body, Function<T, D> mapper, Class<T> clazz) {
        return mapper.apply(httpClient.parseSingle(httpClient.post(url(domain.resource() + "/" + id, Map.of("include", domain.include())), json(body)), clazz));
    }

    private String url(String path, Map<String, String> query) {
        Map<String, String> p = new TreeMap<>(query);
        p.put("format", "json"); p.put("access_token", properties.accessToken());
        String q = p.entrySet().stream().filter(e -> e.getValue() != null && !e.getValue().isBlank()).map(e -> e.getKey() + "=" + httpClient.encode(e.getValue())).collect(java.util.stream.Collectors.joining("&"));
        return properties.baseUrl() + "/api/v1/" + path + (q.isEmpty() ? "" : "?" + q);
    }

    private String json(Map<String, Object> body) {
        try { return mapper.writeValueAsString(body); } catch (Exception e) { throw new TargetProcessClientException("Failed to serialize", e); }
    }
}
