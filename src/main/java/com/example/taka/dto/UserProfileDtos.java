package com.example.taka.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserProfileDtos {

    //what client sends when creating or updating userProfile
    public static record CreateUserProfileDto (
        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 50, message = "First name cannot exceed characters")
         String fName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 50, message = "Last name cannot exceed characters")
         String lName,

        @NotBlank(message = "email is required")
        @Size(min = 2, max = 100, message = "Email cannot exceed characters")
        @Email(message = "Use correct email format")
         String email,

        @NotBlank(message = "password is required")
        @Size(min = 6, max = 100, message = "Password should be at least 6 characters")
         String password,

        @Size(max = 250, message = "cannot exceed characters")
         String bio,

        @Size(max = 255, message = "Profile image url cant exceed 250 characters")
         String profileImage,

        @Pattern(regexp = "^[0-9 ()+-]*$", message = "Phone number contains invalid characters")
        @Size(max = 20, message = "Phone number cannot exceed 20 characters")
         String phoneNumber
    ){}

    //what server returns after client creates or updates userProfile
    public static record UserProfileResponseDto(
      Long id,
      String fName,
      String lName,
      String email,
      String bio,
      String profileImage,
      String phoneNumber,
      boolean enabled,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      String role
    ){}
}
