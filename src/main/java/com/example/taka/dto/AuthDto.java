package com.example.taka.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class AuthDto {
    public record AuthRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String password
    ){}

    public record AuthResponse(
            String token
    ){}



}
