package com.ibm.mcp.zdtp.comment.control;

import com.ibm.mcp.zdtp.comment.entity.Comment;
import com.ibm.mcp.zdtp.comment.entity.CommentDto;
import com.ibm.mcp.zdtp.shared.entity.Owner;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Pattern;

public class CommentConverter {
    private static final Pattern TP_DATE = Pattern.compile("/Date\\((\\d+)[+-]\\d{4}\\)/");

    public CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.id(),
                comment.description(),
                Optional.ofNullable(comment.owner()).map(Owner::login).orElse("N/A"),
                parseDate(comment.createDate()),
                Optional.ofNullable(comment.general()).map(Comment.GeneralRef::id).orElse(0),
                Optional.ofNullable(comment.general()).map(Comment.GeneralRef::name).orElse("N/A")
        );
    }

    private String parseDate(String raw) {
        if (raw == null) return null;
        var m = TP_DATE.matcher(raw);
        if (m.find()) {
            return Instant.ofEpochMilli(Long.parseLong(m.group(1)))
                    .atZone(ZoneOffset.UTC).toLocalDate().toString();
        }
        return raw;
    }
}
