package AuthCentral.service.impl;

import AuthCentral.dto.SignupDto;
import AuthCentral.exception.UserAlreadyExistsException;
import AuthCentral.model.User;
import AuthCentral.repository.UserRepository;
import AuthCentral.service.UserService;
import AuthCentral.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    public User signup(SignupDto signupDto) throws IllegalArgumentException{

        String email = signupDto.getEmail();

        // Check for existing User/email
        User existingUser = userRepository.findByEmail(email);
        if(existingUser != null){
            throw new UserAlreadyExistsException(existingUser.getEmail());
        }

        // // Map DTO to entity
        User user = new User();
        user.setUsername(signupDto.getUsername());
        user.setEmail(signupDto.getEmail());
        user.setPassword(signupDto.getPassword());
        user.setDOB(signupDto.getDob());
        user.setPhone(signupDto.getPhone());

        // Save the user
        return userRepository.save(user);
    }

    public User login(String email, String password){

        // System.out.println("email " +email);

        User loggedInUser = userRepository.findByEmail(email);
        System.out.println("loggedInUser "+loggedInUser);

        if (loggedInUser == null){
            throw new IllegalArgumentException("User does not exist");
        }
        if(!password.equals(loggedInUser.getPassword())){
            throw new IllegalArgumentException("Username or password is incorrect");
        }
        return loggedInUser;
    }
}
