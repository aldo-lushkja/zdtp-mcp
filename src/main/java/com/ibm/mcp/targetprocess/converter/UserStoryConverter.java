package com.ibm.mcp.targetprocess.converter;

import com.ibm.mcp.targetprocess.dto.UserStoryDto;
import com.ibm.mcp.targetprocess.model.EntityState;
import com.ibm.mcp.targetprocess.model.Owner;
import com.ibm.mcp.targetprocess.model.Project;
import com.ibm.mcp.targetprocess.model.UserStory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserStoryConverter {

    public UserStoryDto toDto(UserStory story) {
        return UserStoryDto.builder()
                .id(story.id()).name(story.name()).description(story.description())
                .projectName(extractProjectName(story)).state(extractState(story))
                .ownerLogin(extractOwnerLogin(story))
                .createdAt(story.createDate())
                .build();
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

}
