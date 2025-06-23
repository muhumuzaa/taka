package com.example.taka.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class AuthDto {

    //When registering, you require all names
    public record RegisterRequest(
            @NotBlank(message = "First name is required")
            @Size(min = 2, max = 50)
            String firstName,

            @NotBlank(message = "Last name is required")
            @Size(min = 2, max = 50)
            String lastName,

            @NotBlank
            @Email
            String email,

            @NotBlank
            String password
    ){}

    //for logging. no need to provide f & l names
    public record AuthRequest(
            @NotBlank @Email String email,
            @NotBlank           String password
    ) {}



    public record AuthResponse(
            String token
    ){}



}
