package com.ibm.mcp.zdtp.shared.exception;

public class TargetProcessApiException extends RuntimeException {
    private final int statusCode;

    public TargetProcessApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
