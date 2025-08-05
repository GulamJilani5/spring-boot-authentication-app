package AuthCentral.service.impl;

import AuthCentral.dto.SignupDto;
import AuthCentral.exception.UserAlreadyExistsException;
import AuthCentral.model.User;
import AuthCentral.repository.UserRepository;
import AuthCentral.service.UserService;
import AuthCentral.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(BCryptPasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public User signup(SignupDto signupDto) throws IllegalArgumentException{

        String email = signupDto.getEmail();

        // Check for existing User/email
        Optional<User> existingUser = userRepository.findByEmail(email);

        if(existingUser.isPresent()){
            throw new UserAlreadyExistsException(existingUser.get().getEmail());
        }

        // // Map DTO to entity
        User user = new User();
        user.setUsername(signupDto.getUsername());
        user.setEmail(signupDto.getEmail());
        user.setPassword(passwordEncoder.encode(signupDto.getPassword())); // Hash Password
        user.setDOB(signupDto.getDob());
        user.setPhone(signupDto.getPhone());

        // Save the user
        return userRepository.save(user);
    }


    public User login(String email, String password){
        // System.out.println("email " +email);

        // Find user by email
        Optional<User> loggedInUser = userRepository.findByEmail(email);
        System.out.println("loggedInUser "+loggedInUser);
        if (loggedInUser.isEmpty()){
            throw new IllegalArgumentException("User does not exist");
        }

        // Verify password
        if (!passwordEncoder.matches(password, loggedInUser.get().getPassword())) {
            throw new IllegalArgumentException("Username or password is incorrect!!!");
        }

        return loggedInUser.get();

    }
}
