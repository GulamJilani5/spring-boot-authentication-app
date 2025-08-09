package AuthCentral.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor // Needed for serialization/deserialization
public class UserDto {
    private String username;
    private String email;
    private String dob;
    private String phone;
    private String role;
}
