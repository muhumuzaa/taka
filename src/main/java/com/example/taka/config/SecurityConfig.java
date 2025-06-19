package com.example.taka.config; // Defines the package where this configuration class resides.

import com.example.taka.security.JwtAuthenticationFilter; // Imports a custom filter responsible for handling JWT authentication.
import com.example.taka.services.CustomUserDetailsService; // Imports a service to load user-specific data during authentication.
import lombok.RequiredArgsConstructor; // Lombok annotation to automatically generate a constructor with required arguments (final fields).
import org.springframework.context.annotation.Bean; // Imports @Bean annotation, used to declare a method that produces a bean to be managed by the Spring container.
import org.springframework.context.annotation.Configuration; // Imports @Configuration annotation, marking this class as a source of bean definitions.
import org.springframework.http.HttpMethod; // Imports HttpMethod enum, used to specify HTTP request methods (e.g., POST, GET).
import org.springframework.security.authentication.AuthenticationManager; // Imports AuthenticationManager, the core interface for authentication in Spring Security.
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder; // (Note: This import is not directly used in the provided code, but is commonly used for configuring authentication providers).
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // Imports AuthenticationConfiguration, used to obtain the AuthenticationManager.
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Imports @EnableMethodSecurity, enables Spring Security annotations on methods (e.g., @PreAuthorize).
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // Imports HttpSecurity, used to configure web security for specific HTTP requests.
import org.springframework.security.config.http.SessionCreationPolicy; // Imports SessionCreationPolicy, defines how sessions are managed (e.g., STATELESS).
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Imports BCryptPasswordEncoder, a strong password hashing algorithm.
import org.springframework.security.crypto.password.PasswordEncoder; // Imports PasswordEncoder interface, for encoding and matching passwords.
import org.springframework.security.web.SecurityFilterChain; // Imports SecurityFilterChain, the main component that processes HTTP requests through a chain of filters.
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Imports Spring Security's default filter for username/password authentication.

import static org.antlr.v4.runtime.atn.SemanticContext.and; // (Note: This import seems unused and can likely be removed).

@Configuration // Marks this class as a Spring configuration class, meaning it contains bean definitions.
@RequiredArgsConstructor // Lombok annotation that generates a constructor for final fields (userDetailsService and jwtFilter).
@EnableMethodSecurity // Enables Spring Security's method-level security, allowing annotations like @PreAuthorize, @PostAuthorize, etc.
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService; // Injects a service to retrieve user details (e.g., from a database).
    private final JwtAuthenticationFilter jwtFilter; // Injects the custom JWT authentication filter.

    @Bean // Declares a Spring bean for password encoding.
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Returns an instance of BCryptPasswordEncoder for secure password hashing.
    }

    // Exposes the AuthenticationManager as a Spring bean.
    // The AuthenticationManager is crucial for authenticating users (e.g., during login).
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager(); // Retrieves the AuthenticationManager from the provided configuration.
    }

    @Bean // Declares a Spring bean for the security filter chain. This is where the core security rules are defined.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                // Disables Cross-Site Request Forgery (CSRF) protection.
                // This is often done in REST APIs that use token-based authentication (like JWTs) instead of cookies for session management.
                .csrf(csrf -> csrf.disable())
                // Configures session management to be stateless.
                // This means no session data will be stored on the server side, which is typical for JWT-based authentication.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Defines authorization rules for HTTP requests.
                .authorizeHttpRequests(authz -> authz
                        // Allows unauthenticated access to login and register endpoints (POST requests).
                        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register").permitAll()
                        // Allows unauthenticated access to GET requests for "/api/requests/**" (e.g., fetching public requests).
                        .requestMatchers(HttpMethod.GET, "/api/requests/**").permitAll()
                        // Requires authentication for any other request not explicitly permitted above.
                        .anyRequest().authenticated()
                )
                // Adds the custom JwtAuthenticationFilter before Spring Security's default UsernamePasswordAuthenticationFilter.
                // This ensures that JWTs are processed and authenticated before traditional username/password authentication.
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build(); // Builds and returns the configured SecurityFilterChain.
    }
}