//package AuthCentral.dto;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import org.springframework.http.HttpStatus;
//import java.time.LocalDateTime;
//import java.util.Map;
//
//@Data
//@AllArgsConstructor
//public class ErrorResponseDto {
//    private LocalDateTime timestamp;
//    private int status;
//    private String error;
//    private String message;
//    private String path;
//    private String errorCode; // Custom business error code
//    private String traceId;   // For distributed tracing
//    private Map<String, String> validationErrors;
//
//    public ErrorResponseDto() {
//        this.timestamp = LocalDateTime.now();
//    }
//
//}
