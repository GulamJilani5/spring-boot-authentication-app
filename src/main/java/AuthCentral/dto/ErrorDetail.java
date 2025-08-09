package AuthCentral.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorDetail {
    private String errorCode;  // BUSINESS-1001
    private String field;      // email, password, etc.
    private String message;    // Human readable message
}


