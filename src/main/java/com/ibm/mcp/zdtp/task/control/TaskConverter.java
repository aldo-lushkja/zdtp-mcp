package com.ibm.mcp.zdtp.task.control;

import com.ibm.mcp.zdtp.shared.entity.EntityState;
import com.ibm.mcp.zdtp.shared.entity.Owner;
import com.ibm.mcp.zdtp.shared.entity.Project;
import com.ibm.mcp.zdtp.task.entity.Task;
import com.ibm.mcp.zdtp.task.entity.TaskDto;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.regex.Pattern;

public class TaskConverter {
    private static final Pattern TP_DATE = Pattern.compile("/Date\\((\\d+)[+-]\\d{4}\\)/");

    public TaskDto toDto(Task task) {
        return new TaskDto(
                task.id(),
                task.name(),
                task.description(),
                Optional.ofNullable(task.project()).map(Project::name).orElse(null),
                Optional.ofNullable(task.state()).map(EntityState::name).orElse(null),
                Optional.ofNullable(task.owner()).map(Owner::login).orElse(null),
                parseDate(task.createDate()),
                Optional.ofNullable(task.userStory()).map(Task.UserStoryRef::id).orElse(null),
                Optional.ofNullable(task.userStory()).map(Task.UserStoryRef::name).orElse(null)
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
