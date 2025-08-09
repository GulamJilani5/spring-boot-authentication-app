package AuthCentral.service;

import AuthCentral.dto.SignupDto;
import AuthCentral.model.User;

public interface UserService {

    User signup(SignupDto signupDto);

    User login(String email, String password);


}
