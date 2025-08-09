package AuthCentral.service;

import AuthCentral.dto.ResponseDto;
import AuthCentral.dto.SignupDto;
import AuthCentral.model.User;
import org.springframework.http.ResponseEntity;

public interface UserService {

    User signup(SignupDto signupDto);

    User login(String email, String password);


    // ResponseEntity<ResponseDto> getUsers();

}
