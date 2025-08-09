package AuthCentral.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final MyUserDetailsService myUserDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(MyUserDetailsService myUserDetailsService, JwtAuthFilter jwtAuthFilter) {
        System.out.println("...........SecurityConfig initialized with JwtAuthFilter...........");
        this.myUserDetailsService = myUserDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("...........SecurityConfig SecurityFilterChain...........");
        http
                .csrf(csrf -> csrf.disable())  // No need for CSRF in stateless APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/signup", "/user/login").permitAll()  // Public endpoints
                        .anyRequest().authenticated()  // Everything else requires JWT
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No sessions
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Add our JWT filter

        return http.build();

    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        System.out.println("...........SecurityConfig BCryptPasswordEncoder...........");
        return  new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        System.out.println("SecurityConfig AuthenticationManager");
        return authenticationConfiguration.getAuthenticationManager();
    }

}
