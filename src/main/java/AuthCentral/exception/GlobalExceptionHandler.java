package AuthCentral.exception;

import AuthCentral.dto.ApiResponse;
import AuthCentral.dto.ErrorDetail;

import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


// Approach 1 - Useful for simple applications
/*
//@ControllerAdvice
//public class GlobalExceptionHandler {
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
//            errors.put(error.getField(), error.getDefaultMessage());
//        }
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
//    }
//
//}
*/

// Approach - 2 (Enterprise Grade)
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    // Handle Validation Errors
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        System.out.println("Error:  MethodArgumentNotValidException ex");

        String requestId = UUID.randomUUID().toString();
        String traceId = UUID.randomUUID().toString(); // Replace with tracing framework in production

       // Tracking error in the fields Using stream
        List<ErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorDetail(
                        fieldError.getField(),
                        fieldError.getDefaultMessage(),
                        "VALIDATION_ERROR"))
                .collect(Collectors.toList());

        //  API Error response
        ApiResponse<Void> errorResponse = ApiResponse.error(
                errors,
                "Validation Failed",
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );

        // Send Error Response
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    // Handle Illegal Argument
    @ExceptionHandler(IllegalArgumentException.class)
     // public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request, WebRequest webRequest) {

        System.out.println("Error: IllegalArgumentException.class");
        //Build Error Response || Approach 1 ( Using Map)
        /*
        Map<String, String> validationErrors = new HashMap<>();
        List<ObjectError> validationErrorList = ex.getBindingResult().getAllErrors();
        validationErrorList.forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String validationMsg = error.getDefaultMessage();
            validationErrors.put(fieldName, validationMsg);
        });
         */
        // Approach 2 (DTO - ApiResponse)
        //  API Error response
        ApiResponse<Void> errorResponse = ApiResponse.error(
                List.of(new ErrorDetail(null, ex.getMessage(), "INVALID_ARGUMENT")),
                ex.getMessage(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );

        // Send Error Response
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    // Handles direct JsonMappingException cases when JSON fields cannot be mapped to Java object fields
    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<ApiResponse<Void>> handleJsonMappingException(
            JsonMappingException ex,
            WebRequest request) {

        String requestId = UUID.randomUUID().toString();
        String traceId = UUID.randomUUID().toString();
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
//        logger.error("JsonMappingException at {}: {} [requestId={}, traceId={}]",
//                path, ex.getMessage(), requestId, traceId);

        ApiResponse<Void> errorResponse = ApiResponse.error(
                List.of(new ErrorDetail(null, ex.getMessage(), "REQ_002")),
                "Invalid request body: " + ex.getOriginalMessage(),
                requestId,
                traceId
        );


        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle DB Constraint Violation
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        System.out.println("Error: DataIntegrityViolationException.class");

        String requestId = UUID.randomUUID().toString();
        String traceId = UUID.randomUUID().toString();
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
//        logger.error("DataIntegrityViolationException at {}: {} [requestId={}, traceId={}]",
//                path, ex.getMessage(), requestId, traceId);

        ApiResponse<Void> errorResponse = ApiResponse.error(
                List.of(new ErrorDetail(null, "Database constraint violation", "DB_CONSTRAINT_ERROR")),
                "Database constraint violation",
                requestId,
                traceId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // Handle SQL Errors
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ApiResponse<Void>> handleSQLException(SQLException ex, HttpServletRequest request) {
        System.out.println("Error: SQLException.class");
        String requestId = UUID.randomUUID().toString();
        String traceId = UUID.randomUUID().toString();
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
//        logger.error("SQLException at {}: {} [requestId={}, traceId={}]",
//                path, ex.getMessage(), requestId, traceId);

        ApiResponse<Void> errorResponse = ApiResponse.error(
                List.of(new ErrorDetail(null, "Database error", "SQL_ERROR")),
                "Database error",
                requestId,
                traceId
        );

//        log.error("SQL error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // // Handles cases where the request body is unreadable or contains malformed/unknown JSON fields
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String requestId = UUID.randomUUID().toString();
        String traceId = UUID.randomUUID().toString();
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        String errorMessage = "Invalid request body format. Check for unknown or malformed fields.";
        String errorCode = "REQ_001";

        if (ex.getCause() instanceof com.fasterxml.jackson.databind.JsonMappingException jsonEx) {
            errorMessage = "Invalid JSON: " + jsonEx.getOriginalMessage();
            errorCode = "REQ_002";
        }

//        logger.error("HttpMessageNotReadableException at {}: {} [requestId={}, traceId={}]",
//                path, errorMessage, requestId, traceId);

        ApiResponse<Void> errorResponse = ApiResponse.error(
                List.of(new ErrorDetail(null, errorMessage, errorCode)),
                errorMessage,
                requestId,
                traceId
        );


        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
}


    // Add more exception handlers as needed, e.g., for custom exceptions...
    //...
    //...

    // Handle Custom Business Exception
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        String requestId = UUID.randomUUID().toString();
        String traceId = UUID.randomUUID().toString(); // Replace with tracing framework in production
//        logger.error("UserAlreadyExistsException: {} [requestId={}, traceId={}]", ex.getMessage(), requestId, traceId);

        ApiResponse<Void> errorResponse = ApiResponse.error(
                List.of(new ErrorDetail(null, ex.getMessage(), "USER_ALREADY_EXISTS")),
                ex.getMessage(),
                requestId,
                traceId
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // Handle All Other Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex,HttpServletRequest request, WebRequest webRequest) {

        System.out.println("Error: Exception.class");
        String requestId = UUID.randomUUID().toString();
        String traceId = UUID.randomUUID().toString();

//        logger.error("UserAlreadyExistsException: {} [requestId={}, traceId={}]", ex.getMessage(), requestId, traceId);

        ApiResponse<Void> errorResponse = ApiResponse.error(
                List.of(new ErrorDetail(null, ex.getMessage(), "USER_ALREADY_EXISTS")),
                ex.getMessage(),
                requestId,
                traceId
        );

        // log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
