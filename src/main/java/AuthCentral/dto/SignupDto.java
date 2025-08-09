package AuthCentral.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

//@JsonIgnoreProperties(ignoreUnknown = false)
@Data
public class SignupDto {

    private String username;

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "password must not be empty")
    @Size(min = 5, message = "Password must be at least 5 character")
    private String password;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phone;

    // Example: enforce format like "dd/MM/yyyy"
    @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$",
            message = "DOB must be in valid dd/MM/yyyy format")
    private String dob;

    @Pattern(regexp = "ROLE_USER|ROLE_ADMIN", message = "Role must be either ROLE_USER or ROLE_ADMIN")
    private String role;

}
