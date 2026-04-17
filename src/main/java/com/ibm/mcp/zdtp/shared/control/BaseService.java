package com.ibm.mcp.zdtp.shared.control;

import com.ibm.mcp.zdtp.shared.odata.ODataQueryBuilder;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.util.MarkdownConverter;

import java.util.Map;
import java.util.TreeMap;

public abstract class BaseService {
    protected final QueryEngine engine;

    protected BaseService(QueryEngine engine) {
        this.engine = engine;
    }

    protected ODataQueryBuilder query() {
        return new ODataQueryBuilder();
    }

    protected String convertMarkdown(String content) {
        return MarkdownConverter.toHtml(content);
    }

    protected Map<String, String> searchParams(String whereClause, int take) {
        var params = new TreeMap<String, String>();
        if (whereClause != null && !whereClause.isBlank()) {
            params.put("where", whereClause);
        }
        params.put("take", String.valueOf(take));
        return params;
    }

    protected Map<String, String> searchParamsWithOrder(String whereClause, int take, String orderBy) {
        var params = searchParams(whereClause, take);
        if (orderBy != null && !orderBy.isBlank()) {
            params.put("orderByDesc", orderBy);
        }
        return params;
    }
}