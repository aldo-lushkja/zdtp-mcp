package com.ibm.mcp.zdtp.shared.control;

import com.ibm.mcp.zdtp.shared.odata.ODataQueryBuilder;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.util.MarkdownConverter;

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
}
