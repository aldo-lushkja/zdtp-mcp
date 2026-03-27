package com.ibm.mcp.zdtp.user.control;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.user.entity.UserDto;

public class UserSearchService extends BaseService {
    private final UserConverter converter;

    public UserSearchService(QueryEngine engine, UserConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public List<UserDto> search(String query, int take) {
        String whereClause = query()
                .add("(FirstName contains '%s') or (Login contains '%s')".formatted(query, query))
                .build();

        Map<String, String> parameters = new TreeMap<>();
        if (!whereClause.isBlank()) {
            parameters.put("where", whereClause);
        }
        parameters.put("take", String.valueOf(take));

        return engine.list(QueryEngine.USER, parameters, new TypeReference<>() {}, converter::toDto);
    }
}
