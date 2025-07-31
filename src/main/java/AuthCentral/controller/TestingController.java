package AuthCentral.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/test")
public class TestingController {

//    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);


    @PostMapping("/filter/{id}")
    public ResponseEntity<Map<String, String>> filterMethod(@PathVariable String id,
                                            @RequestParam Map<String, String> reqParam,
                                            @RequestHeader Map<String, String> reqHeader,
                                            @RequestBody Map<String, String> reqBody) throws JsonProcessingException {

        String jsonParam = objectMapper.writeValueAsString(reqParam);
        String jsonReq = objectMapper.writeValueAsString(reqBody);
        String jsonHeader = objectMapper.writeValueAsString(reqHeader);

        // Path Variable
         System.out.println("@PathVariable - id: " + id);
        //Request Param(Query Parameter)
         System.out.println("@RequestParam - request parameter: " + jsonParam);
        // Header
        ///// Request Header
        System.out.println("@RequestHeader - headers: " + jsonHeader);
        ///// Response Header
        // Basic validation
        if (reqBody == null || !reqBody.containsKey("username") || !reqBody.containsKey("password")) {
            // Create custom headers for error response
            HttpHeaders errorHeaders = new HttpHeaders();
            errorHeaders.add("X-Request-ID", UUID.randomUUID().toString());
            errorHeaders.add("X-Error-Code", "INVALID_INPUT");

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("Message", "fail");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) // HTTP 400
                    .headers(errorHeaders)               // Custom headers
                    .body(errorResponse); // Response body
        }

        // Create custom headers for success response
        HttpHeaders successHeaders = new HttpHeaders();
        successHeaders.add("X-Request-ID", UUID.randomUUID().toString());
        successHeaders.add("Location", "/user/" + reqBody.get("username")); // Example: Resource location

        //Request Body
        System.out.println("@RequestBody - request body: " + jsonReq);


        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("Message", "Success");
        return ResponseEntity
                             .status(HttpStatus.CREATED)
                             .headers(successHeaders)
                             .body(successResponse);
    }
}




