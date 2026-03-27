package com.ibm.mcp.zdtp.user.control;

import com.ibm.mcp.zdtp.user.entity.User;
import com.ibm.mcp.zdtp.user.entity.UserDto;

public class UserConverter {
    public UserDto toDto(User user) {
        return new UserDto(
                user.id(),
                user.firstName(),
                user.lastName(),
                user.login(),
                user.email(),
                user.isActive() != null && user.isActive()
        );
    }
}
