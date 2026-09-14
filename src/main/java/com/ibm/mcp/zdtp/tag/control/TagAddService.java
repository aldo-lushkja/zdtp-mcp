package com.ibm.mcp.zdtp.tag.control;

import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.tag.entity.AssignableTagDto;
import com.ibm.mcp.zdtp.tag.entity.TagResult;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TagAddService {
    private static final QueryEngine.Domain ASSIGNABLE_TAG_DOMAIN = new QueryEngine.Domain("Assignables", "[Id,Tags]");
    private final QueryEngine engine;

    public TagAddService(QueryEngine engine) {
        this.engine = engine;
    }

    public TagResult addTag(int entityId, String tagToAdd) {
        AssignableTagDto current = engine.get(ASSIGNABLE_TAG_DOMAIN, entityId, dto -> dto, AssignableTagDto.class);
        Set<String> tagSet = new LinkedHashSet<>();
        if (current.tags() != null && !current.tags().isBlank()) {
            Arrays.stream(current.tags().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .forEach(tagSet::add);
        }
        tagSet.add(tagToAdd.trim());
        String newTagsStr = String.join(", ", tagSet);

        AssignableTagDto updated = engine.update(
                ASSIGNABLE_TAG_DOMAIN,
                entityId,
                Map.of("Tags", newTagsStr),
                dto -> dto,
                AssignableTagDto.class
        );

        return new TagResult(updated.id(), updated.tags());
    }
}
