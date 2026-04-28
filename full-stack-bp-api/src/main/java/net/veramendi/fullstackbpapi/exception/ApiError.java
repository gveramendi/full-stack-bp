package net.veramendi.fullstackbpapi.exception;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldIssue> issues
) {

    public record FieldIssue(String field, String message) {}

    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(Instant.now(), status, error, message, path, List.of());
    }

    public static ApiError withIssues(int status, String error, String message, String path, List<FieldIssue> issues) {
        return new ApiError(Instant.now(), status, error, message, path, issues);
    }
}
