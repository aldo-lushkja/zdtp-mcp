package com.ibm.mcp.zdtp.shared.odata;

import java.util.List;
import java.util.ArrayList;
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

    public record Domain(String resource, String include, String where) {
        public Domain(String resource, String include) {
            this(resource, include, null);
        }
    }

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
    public static final Domain USER = new Domain("Users", "[Id,FirstName,LastName,Login,Email,IsActive]");
    public static final Domain RELATION = new Domain("GeneralRelations", "[Id,RelationType[Id,Name],InboundGeneral[Id,Name,ResourceType],OutboundGeneral[Id,Name,ResourceType]]");

    public <T, D> List<D> list(Domain domain, Map<String, String> params, TypeReference<TargetProcessResponse<T>> typeRef, Function<T, D> mapper) {
        Map<String, String> parameters = new TreeMap<>(params);
        if (domain.include() != null && !domain.include().isBlank()) {
            parameters.put("include", domain.include());
        }
        if (domain.where() != null && !domain.where().isBlank()) {
            parameters.put("where", domain.where());
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
        StringBuilder sb = new StringBuilder();
        sb.append(properties.baseUrl());
        sb.append("/api/v1/");
        sb.append(path);
        sb.append("?access_token=");
        sb.append(properties.accessToken());
        sb.append("&format=json");
        
        // Use a linked map to ensure deterministic order if needed, 
        // but the key is to ensure we don't have any strange characters.
        List<String> queryParams = new ArrayList<>();
        for (Map.Entry<String, String> entry : query.entrySet()) {
            String value = entry.getValue();
            if (value != null && !value.isBlank()) {
                queryParams.add(entry.getKey() + "=" + TargetProcessHttpClient.encode(value));
            }
        }
        
        if (!queryParams.isEmpty()) {
            sb.append("&");
            sb.append(String.join("&", queryParams));
        }
        
        return sb.toString();
    }

    private String toJson(Map<String, Object> body) {
        try {
            return mapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new TargetProcessClientException("Failed to serialize request body", e);
        }
    }
}
