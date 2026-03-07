package com.ibm.mcp.targetprocess.userstory.converter;

import com.ibm.mcp.targetprocess.shared.model.EntityState;
import com.ibm.mcp.targetprocess.shared.model.Owner;
import com.ibm.mcp.targetprocess.shared.model.Project;
import com.ibm.mcp.targetprocess.shared.model.ReleaseReference;
import com.ibm.mcp.targetprocess.shared.model.SprintReference;
import com.ibm.mcp.targetprocess.userstory.dto.UserStoryDto;
import com.ibm.mcp.targetprocess.userstory.model.UserStory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class UserStoryConverter {

    private static final Pattern TP_DATE = Pattern.compile("/Date\\((\\d+)[+-]\\d{4}\\)/");

    public UserStoryDto toDto(UserStory story) {
        return UserStoryDto.builder()
                .id(story.id()).name(story.name()).description(story.description())
                .projectName(extractProjectName(story)).state(extractState(story))
                .ownerLogin(extractOwnerLogin(story))
                .assigneeLogin(extractAssigneeLogin(story))
                .effort(story.effort())
                .createdAt(parseDate(story.createDate()))
                .endDate(parseDate(story.endDate()))
                .releaseId(Optional.ofNullable(story.release()).map(ReleaseReference::id).orElse(null))
                .releaseName(Optional.ofNullable(story.release()).map(ReleaseReference::name).orElse(null))
                .sprintId(Optional.ofNullable(story.sprint()).map(SprintReference::id).orElse(null))
                .sprintName(Optional.ofNullable(story.sprint()).map(SprintReference::name).orElse(null))
                .build();
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
        return Optional.ofNullable(story.assignedUser()).map(Owner::login).orElse(null);
    }
}
