package AuthCentral.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtil;
    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        System.out.println("+++++++++++++++++++++JwtAuthFilter shouldNotFilter++++++++++++++++++++");

        String path = request.getServletPath();
        return path.equals("/user/signup") || path.equals("/user/login");
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        System.out.println("++++++++++++++++++++++JwtAuthFilter doFilterInternal++++++++++++++++++");
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ") && authHeader.length() > 7) {
                token = authHeader.substring(7);
                username = jwtUtil.extractUsername(token);
                System.out.println("doFilterInternal, Extracted username from JWT: " + username);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("doFilterInternal, Authenticated user: " + username);
                }else {
                    System.out.println("doFilterInternal, Invalid JWT token for user: " + username);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\": \"Invalid JWT token\"}");
                    return; // Stop filter chain
                }
            }
        } catch (Exception e) {
            System.out.println("doFilterInternal, JWT processing failed: " + e.getMessage());
            new Exception("JWT processing failed");
            return;
        }

        chain.doFilter(request, response);
    }
}