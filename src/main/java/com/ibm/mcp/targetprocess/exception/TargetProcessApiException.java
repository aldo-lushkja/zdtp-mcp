package com.ibm.mcp.targetprocess.exception;

public class TargetProcessApiException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    public TargetProcessApiException(int statusCode, String responseBody) {
        super("TargetProcess API returned HTTP " + statusCode + ": " + responseBody);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() { return statusCode; }
    public String getResponseBody() { return responseBody; }
}
