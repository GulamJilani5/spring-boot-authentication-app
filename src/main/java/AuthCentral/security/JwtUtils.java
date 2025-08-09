package AuthCentral.security;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private final Key SECRET_KEY = Keys.hmacShaKeyFor("your-very-strong-secret-key-which-is-32-chars".getBytes());
    // Better to use env variable
    private final long EXPIRATION = 24 * 60 * 60 * 1000; // 24 hours


    public String generateToken(String username){
        System.out.println("==========JwtUtils generateToken=============");

        return Jwts
                .builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();

    }

    public String extractUsername(String token){
        System.out.println("==============JwtUtils extractUsername======================");

        return Jwts
                .parserBuilder()                       // New API
                .setSigningKey(SECRET_KEY)      // Use byte[] for key
                .build()
                .parseClaimsJws(token)
                .getBody()              // Get the JWT body (Claims).
                .getSubject();         // Extract the "sub" (subject) field (usually username/email)
    }

    public boolean validateToken(String token){
        System.out.println("============JwtUtils validateToken===============");

        try{
            Jwts
                .parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token);

           return true;

        }catch (JwtException | IllegalArgumentException e){
            return false;

        }

    }

}
