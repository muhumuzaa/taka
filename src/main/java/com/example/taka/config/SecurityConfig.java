package com.example.taka.config;

import com.example.taka.security.JwtAuthenticationFilter;
import com.example.taka.services.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.antlr.v4.runtime.atn.SemanticContext.and;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //expose authenticationManager to AuthController to call authenticate()
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                //disable CSRF since im not using cookies
                .csrf(csrf -> csrf.disable())
                //make sessions stateless. no storing anythin in securityContext
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //defining public and private endpoints
                .authorizeHttpRequests(authz -> authz
                //allow login and register
                                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register").permitAll()
                        //allow public GETS
                                .requestMatchers(HttpMethod.GET, "/api/requests/**").permitAll()
                        //restrict other endpoints
                                .anyRequest().authenticated()
                )
                //Add my custom filter before Spring's UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
