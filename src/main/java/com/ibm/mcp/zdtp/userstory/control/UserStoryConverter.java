package com.ibm.mcp.zdtp.userstory.control;

import com.ibm.mcp.zdtp.shared.entity.EntityState;
import com.ibm.mcp.zdtp.shared.entity.Owner;
import com.ibm.mcp.zdtp.shared.entity.Project;
import com.ibm.mcp.zdtp.shared.entity.ReleaseReference;
import com.ibm.mcp.zdtp.shared.entity.SprintReference;
import com.ibm.mcp.zdtp.shared.entity.TeamReference;
import com.ibm.mcp.zdtp.userstory.entity.UserStoryDto;
import com.ibm.mcp.zdtp.userstory.entity.UserStory;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Pattern;
import com.ibm.mcp.zdtp.shared.model.TargetProcessItems;

public class UserStoryConverter {

    private static final Pattern TP_DATE = Pattern.compile("/Date\\((\\d+)[+-]\\d{4}\\)/");

    public UserStoryDto toDto(UserStory story) {
        return new UserStoryDto(
                story.id(),
                story.name(),
                story.description(),
                extractProjectName(story),
                extractState(story),
                extractOwnerLogin(story),
                extractAssigneeLogin(story),
                story.effort(),
                parseDate(story.createDate()),
                parseDate(story.endDate()),
                Optional.ofNullable(story.release()).map(ReleaseReference::id).orElse(null),
                Optional.ofNullable(story.release()).map(ReleaseReference::name).orElse(null),
                Optional.ofNullable(story.sprint()).map(SprintReference::id).orElse(null),
                Optional.ofNullable(story.sprint()).map(SprintReference::name).orElse(null),
                Optional.ofNullable(story.team()).map(TeamReference::name).orElse(null)
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

    private String extractProjectName(UserStory story) {
        return Optional.ofNullable(story.project()).map(Project::name).orElse(null);
    }

    private String extractState(UserStory story) {
        return Optional.ofNullable(story.state()).map(EntityState::name).orElse(null);
    }

    private String extractOwnerLogin(UserStory story) {
        return Optional.ofNullable(story.owner()).map(Owner::login).orElse(null);
    }

    private String extractAssigneeLogin(UserStory story) {
        return Optional.ofNullable(story.assignedUser())
                .map(TargetProcessItems::items)
                .flatMap(items -> items.stream().findFirst())
                .map(Owner::login)
                .orElse(null);
    }
}
