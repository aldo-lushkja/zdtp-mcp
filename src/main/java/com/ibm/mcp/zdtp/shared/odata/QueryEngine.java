package com.ibm.mcp.zdtp.shared.odata;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.model.TargetProcessResponse;
import com.ibm.mcp.zdtp.shared.exception.TargetProcessClientException;

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
    public static final Domain TASK = new Domain("Tasks", "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,Owner[Id,Login],UserStory[Id,Name]]");
    public static final Domain BUG = new Domain("Bugs", "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login],AssignedUser[Id,Login],Release[Id,Name],UserStory[Id,Name],Feature[Id,Name],TeamIteration[Id,Name]]");
    public static final Domain FEATURE = new Domain("Features", "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login],TeamIteration[Id,Name]]");
    public static final Domain EPIC = new Domain("Epics", "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login]]");
    public static final Domain RELEASE = new Domain("Releases", "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,StartDate,EndDate,Effort,Owner[Id,Login]]");
    public static final Domain REQUEST = new Domain("Requests", "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login]]");
    public static final Domain TEST_PLAN = new Domain("TestPlans", "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,Owner[Id,Login]]");
    public static final Domain TEST_CASE = new Domain("TestCases", "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,Owner[Id,Login],TestPlans[Id,Name]]");
    public static final Domain TEST_STEP = new Domain("TestSteps", "[Id,Description,Result,RunOrder,TestCase[Id,Name]]");
    public static final Domain COMMENT = new Domain("Comments", "[Id,Description,CreateDate,Owner[Id,Login],General[Id,Name]]");
    public static final Domain TEAM = new Domain("Teams", "[Id,Name]");
    public static final Domain TEAM_ITERATION = new Domain("TeamIterations", "[Id,Name,StartDate,EndDate,Team[Id,Name]]");
    public static final Domain PROJECT = new Domain("Projects", "[Id,Name]");

    public <T, D> List<D> list(Domain domain, Map<String, String> params, TypeReference<TargetProcessResponse<T>> typeRef, Function<T, D> mapper) {
        Map<String, String> parameters = new TreeMap<>(params);
        if (!domain.include().isBlank()) {
            parameters.putIfAbsent("include", domain.include());
        }
        
        String url = buildUrl(domain.resource(), parameters);
        String responseBody = httpClient.fetch(url);
        TargetProcessResponse<T> response = httpClient.parse(responseBody, typeRef);
        
        return Optional.ofNullable(response.items())
                .orElse(List.of())
                .stream()
                .map(mapper)
                .toList();
    }

    public <T, D> D get(Domain domain, int id, Function<T, D> mapper, Class<T> clazz) {
        Map<String, String> parameters = Map.of("include", domain.include());
        String url = buildUrl(domain.resource() + "/" + id, parameters);
        String responseBody = httpClient.fetch(url);
        T entity = httpClient.parseSingle(responseBody, clazz);
        return mapper.apply(entity);
    }

    public <T, D> D create(Domain domain, Map<String, Object> body, Function<T, D> mapper, Class<T> clazz) {
        Map<String, String> parameters = Map.of("include", domain.include());
        String url = buildUrl(domain.resource(), parameters);
        String jsonBody = toJson(body);
        String responseBody = httpClient.post(url, jsonBody);
        T entity = httpClient.parseSingle(responseBody, clazz);
        return mapper.apply(entity);
    }

    public <T, D> D update(Domain domain, int id, Map<String, Object> body, Function<T, D> mapper, Class<T> clazz) {
        Map<String, String> parameters = Map.of("include", domain.include());
        String url = buildUrl(domain.resource() + "/" + id, parameters);
        String jsonBody = toJson(body);
        String responseBody = httpClient.post(url, jsonBody);
        T entity = httpClient.parseSingle(responseBody, clazz);
        return mapper.apply(entity);
    }

    public void delete(Domain domain, int id) {
        String url = buildUrl(domain.resource() + "/" + id, Map.of());
        httpClient.delete(url);
    }

    private String buildUrl(String path, Map<String, String> query) {
        Map<String, String> params = new TreeMap<>(query);
        params.put("format", "json");
        params.put("access_token", properties.accessToken());
        
        String queryString = params.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().isBlank())
                .map(e -> e.getKey() + "=" + TargetProcessHttpClient.encode(e.getValue()))
                .collect(Collectors.joining("&"));
                
        return properties.baseUrl() + "/api/v1/" + path + (queryString.isEmpty() ? "" : "?" + queryString);
    }

    private String toJson(Map<String, Object> body) {
        try {
            return mapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new TargetProcessClientException("Failed to serialize request body", e);
        }
    }
}
