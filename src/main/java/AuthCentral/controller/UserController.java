package AuthCentral.controller;

import AuthCentral.dto.*;
import AuthCentral.model.User;
import AuthCentral.security.JwtUtils;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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

     @Autowired
     JwtUtils jwtUtils;

     //Signup
     @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserDto>> signup(@Valid @RequestBody SignupDto signupDto) throws JsonProcessingException {

            String jsonOutput = objectMapper.writeValueAsString(signupDto);
            System.out.println("------------UserController, Signup,--------------  input: \n" + jsonOutput);

            // Perform Signup
            User savedUser= userService.signup(signupDto);

            // Build Response || Approach 1 (Using Map)
              /*
                 Map<String, String> signupResponse = new HashMap<>();
                 signupResponse.put("message", "Sign up has done successfully!!!");
                 return ResponseEntity.status(HttpStatus.CREATED).body(signupResponse);
              */

         // Build Response || Approach 2 (Using DTO - ResponseDto)
         // Convert User to UserDto
         UserDto data = new UserDto();
         data.setUsername(savedUser.getUsername());
         data.setEmail(savedUser.getEmail());
         data.setDob(savedUser.getDOB());
         data.setPhone(savedUser.getPhone());
         data.setRole(savedUser.getRole() != null ? savedUser.getRole().name() : "ROLE_USER");

         // Generate IDs for tracking (could integrate with real tracing later)
         String requestId = UUID.randomUUID().toString();
         String traceId = UUID.randomUUID().toString();

         // Use i18n for success message
         // String message = messageSource.getMessage("signup.success", null, "Sign up has done successfully!!!", locale);

           // Build Response
         ApiResponse<UserDto> response = ApiResponse.success(
                 data,
                 "--------------Sign up completed successfully!",
                 requestId,
                 traceId
         );

          // Send Response ( Using DTO-ResponseDto)
         return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //Login
    @PostMapping("/login")
     // public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginBody){
    //    String email = loginBody.get("email");
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginDto loginBody){

            String email = loginBody.getEmail();
            System.out.println(".............UserController, login........... Email: " +email);

        String requestId = UUID.randomUUID().toString();
        String traceId = UUID.randomUUID().toString();

             // 1. Authenticate user
             Authentication authentication = authenticateManager.authenticate(
                     new UsernamePasswordAuthenticationToken(loginBody.getEmail(), loginBody.getPassword())
             );

             // 2. Generate JWT token
             String token = jwtUtils.generateToken(loginBody.getEmail());

             // 3. Build response
             ApiResponse<String> response = ApiResponse.success(
                     token,
                     "Login successful",
                     requestId,
                     traceId
             );

             // 4. Send Response
            return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
