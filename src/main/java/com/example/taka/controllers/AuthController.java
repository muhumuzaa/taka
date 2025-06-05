package com.example.taka.controllers;

import com.example.taka.dto.AuthDto;
import com.example.taka.models.UserProfile;
import com.example.taka.models.UserRole;
import com.example.taka.repos.UserProfileRepository;
import com.example.taka.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserProfileRepository userRepo;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public ResponseEntity<AuthDto.AuthResponse> login(@Valid @RequestBody AuthDto.AuthRequest request){

        //authenticate user using authenitcationManager
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPasswowrd());
        try{
            authManager.authenticate(authToken);
        }catch(BadCredentialsException e){
            return ResponseEntity.status(401).build();
        }

        //if authentication succeeds, generate JWT
        String token = jwtUtil.generateToken(request.getEmail());

        return ResponseEntity.ok(new AuthDto.AuthResponse(token));
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody AuthDto.AuthRequest request){

        //check if user already exists
        if(userRepo.findByEmail(request.getEmail().toLowerCase()).isPresent()){
            return ResponseEntity.status(409).body("Email is already in use");
        }

        //create a new userProfile
        UserProfile newUser = UserProfile.builder()
                .fName("default")
                .lName("default")
                .email(request.getEmail().trim().toLowerCase())
                .user_role(UserRole.USER)
                .enabled(false)
                .build();

        userRepo.save(newUser);
        return ResponseEntity.status(201).body("Account registered successfully");
    }
}
