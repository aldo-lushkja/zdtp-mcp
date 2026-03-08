package com.ibm.mcp.zdtp.comment.control;

import com.ibm.mcp.zdtp.comment.entity.Comment;
import com.ibm.mcp.zdtp.comment.entity.CommentDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import java.util.LinkedHashMap;
import java.util.Map;

public class CommentCreateService extends BaseService {
    private final CommentConverter converter;

    public CommentCreateService(QueryEngine engine, CommentConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public CommentDto addComment(int entityId, String text) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("Description", convertMarkdown(text));
        body.put("General", Map.of("Id", entityId));

        return engine.create(QueryEngine.COMMENT, body, converter::toDto, Comment.class);
    }
}
