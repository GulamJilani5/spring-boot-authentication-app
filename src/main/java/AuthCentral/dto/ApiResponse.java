package AuthCentral.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class ApiResponse<T> { // ✅ Generic here

    public enum Status {
        SUCCESS, ERROR
    }

    private final Status status; // SUCCESS or ERROR
    private final String message; // Default to empty string if null
    private final T data; // Generic type for any payload
    private final List<ErrorDetail> errors;
    private final String requestId; // Default to generated UUID if null
    private final String traceId; // Default to generated UUID if null
    private final LocalDateTime timestamp; // Always non-null

    // Success factory method
    public static <T> ApiResponse<T> success(T data, String message, String requestId, String traceId) {
        return ApiResponse.<T>builder()
                .status(Status.SUCCESS)
                .message(message != null ? message : "")
                .data(data)
                .requestId(requestId != null ? requestId : UUID.randomUUID().toString())
                .traceId(traceId != null ? traceId : UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Error factory method
    public static ApiResponse<Void> error(List<ErrorDetail> errors, String message, String requestId, String traceId) {
        return ApiResponse.<Void>builder()
                .status(Status.ERROR)
                .message(message != null ? message : "An error occurred")
                .errors(errors)
                .requestId(requestId != null ? requestId : UUID.randomUUID().toString())
                .traceId(traceId != null ? traceId : UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
