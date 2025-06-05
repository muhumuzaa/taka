package com.example.taka.models;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(uniqueConstraints ={
        @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    private String fName;

    @NotBlank
    @Size(min = 2, max = 50)
    private String lName;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false, length =100, unique =true)
    @Email(message ="Use correct email format")
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String passwordHarsh;

    @Size(max=250)
    private String bio;

    @Size(max=255, message="Profile image url cant exceed 250 characters")
    private String profileImage;

    @Pattern(regexp = "^[0-9 ()+-]*$", message = "Phone number contains invalid characters")
    @Size(max=20)
    private String phoneNumber;

    @Column(nullable =false)
    private boolean enabled = false;

    @Builder.Default
    @Column(nullable =false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(nullable =false, updatable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole user_role = UserRole.USER;

}
