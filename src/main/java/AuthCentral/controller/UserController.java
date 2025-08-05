package AuthCentral.controller;

import AuthCentral.dto.ResponseDto;
import AuthCentral.dto.LoginDto;
import AuthCentral.dto.SignupDto;
import AuthCentral.model.User;
import AuthCentral.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

     @Autowired
    UserService userService;

     @Autowired
     AuthenticationManager authenticateManager;

     //Signup
     @PostMapping("/signup")
    public ResponseEntity<Object> signup(@Valid @RequestBody SignupDto signupDto) throws JsonProcessingException {

            String jsonOutput = objectMapper.writeValueAsString(signupDto);
            System.out.println("Signup input:\n" + jsonOutput);

            // Perform Signup
            User savedUser= userService.signup(signupDto);

            // Build Response || Approach 1 (Using Map)
              /*
                 Map<String, String> signupResponse = new HashMap<>();
                 signupResponse.put("message", "Sign up has done successfully!!!");
                 return ResponseEntity.status(HttpStatus.CREATED).body(signupResponse);
              */

         // Build Response || Approach 2 (Using DTO - ResponseDto)
           Map<String, String> data = new HashMap<>();
             data.put("username", savedUser.getUsername());
             data.put("email", savedUser.getEmail());
             data.put("dob", savedUser.getDOB());

           ResponseDto<Object> response = new ResponseDto<>(
                 "success",
                 "Sign up has done successfully!!!",
                 data,
                 LocalDateTime.now()
           );

          // Send Response ( Using DTO-ResponseDto)
         return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //Login
    @PostMapping("/login")
     // public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginBody){
    //    String email = loginBody.get("email");
    public ResponseEntity<ResponseDto<String>> login(@RequestBody LoginDto loginBody){

         String email = loginBody.getEmail();
         String password = loginBody.getPassword();
        // System.out.println("email " +email);

        // Perform Login
         User loggedInUser =  userService.login(email, password);

        // Build Response || Approach 2
        ResponseDto<String> response = new ResponseDto<>(
                "success",
                "Sign up has done successfully!!!",
                null,
                LocalDateTime.now()
        );



        // Send Response ( Using DTO-ResponseDto)
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
