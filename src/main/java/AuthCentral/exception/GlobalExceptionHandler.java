package AuthCentral.exception;

import AuthCentral.dto.ErrorResponseDto;
import AuthCentral.dto.ResponseDto;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    private ErrorResponseDto buildErrorResponse(HttpStatus status, String message,
                                                String path, String errorCode,
                                                Map<String, String> validationErrors) {
        return new ErrorResponseDto(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                errorCode,
                UUID.randomUUID().toString(), // Trace ID
                validationErrors
        );
    }


    // Handle Validation Errors
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        System.out.println("Error:  MethodArgumentNotValidException ex");

       // Tracking error in the fields Using Map
        Map<String, String> validationErrors = new HashMap<>();
        List<ObjectError> validationErrorList = ex.getBindingResult().getAllErrors();
        validationErrorList.forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String validationMsg = error.getDefaultMessage();
            validationErrors.put(fieldName, validationMsg);
        });
        //path
        String path = request.getDescription(false).replace("uri=", "");

        ErrorResponseDto errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST,
                "Validation Failed", path, "VALIDATION_ERROR", validationErrors );

        // Send Error Response
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    // Handle Illegal Argument
    @ExceptionHandler(IllegalArgumentException.class)
     // public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request, WebRequest webRequest) {

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
        // Approach 2 (DTO - ErrorResponseDTO)
            ErrorResponseDto errorResponse = buildErrorResponse(HttpStatus.CONFLICT,
                    ex.getMessage(), request.getRequestURI(), "INVALID_ARGUMENT", null
            );

        // Send Error Response
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    // Handles direct JsonMappingException cases when JSON fields cannot be mapped to Java object fields
    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<ErrorResponseDto> handleJsonMappingException(
            JsonMappingException ex,
            WebRequest request) {

        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        ErrorResponseDto errorResponse = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request body: " + ex.getOriginalMessage(),
                path,
                "REQ_002", // custom code for JSON mapping
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle DB Constraint Violation
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        System.out.println("Error: DataIntegrityViolationException.class");

        ErrorResponseDto response = buildErrorResponse(HttpStatus.CONFLICT,
                "Database constraint violation", request.getRequestURI(), "DB_CONSTRAINT_ERROR", null);

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // Handle SQL Errors
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponseDto> handleSQLException(SQLException ex, HttpServletRequest request) {
        System.out.println("Error: SQLException.class");
        ErrorResponseDto response = buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Database error", request.getRequestURI(), "SQL_ERROR", null);

//        log.error("SQL error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // // Handles cases where the request body is unreadable or contains malformed/unknown JSON fields
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

    String path = ((ServletWebRequest) request).getRequest().getRequestURI();
    String errorMessage = "Invalid request body format. Check for unknown or malformed fields.";

    if (ex.getCause() instanceof JsonMappingException jsonEx) {
        // Provide a more specific JSON error message
        errorMessage = "Invalid JSON: " + jsonEx.getOriginalMessage();
    }

    ErrorResponseDto errorResponse = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            errorMessage,
            path,
            "REQ_001", // your custom error code
            null // no validation errors here
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
}


    // Add more exception handlers as needed, e.g., for custom exceptions...
    //...
    //...

    // Handle Custom Business Exception
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyExistsException(UserAlreadyExistsException ex, HttpServletRequest request, WebRequest webRequest) {

        System.out.println("Error: UserAlreadyExistsException.class");

        ErrorResponseDto errorResponse = buildErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(), request.getRequestURI(), "USER_EXISTS", null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle All Other Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex,HttpServletRequest request, WebRequest webRequest) {

        System.out.println("Error: Exception.class");

        ErrorResponseDto response = buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred", request.getRequestURI(), "GENERIC_ERROR", null);

//        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
