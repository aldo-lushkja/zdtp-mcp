package com.ibm.mcp.zdtp.time.control;

import com.ibm.mcp.zdtp.time.entity.TimeEntry;
import com.ibm.mcp.zdtp.time.entity.TimeEntryDto;

public class TimeConverter {
    public TimeEntryDto toDto(TimeEntry t) {
        return new TimeEntryDto(
                t.id() != null ? t.id() : 0,
                t.spent() != null ? t.spent() : 0.0,
                t.description(),
                t.date(),
                t.user() != null ? t.user().login() : null
        );
    }
}
