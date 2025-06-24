package com.example.taka.models;

import com.example.taka.repos.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class seedAdmin implements CommandLineRunner {
    private final UserProfileRepository userRepo;
    private final PasswordEncoder encoder;

    @Value("${seed.admin.password}")
    String adminPassword;

    @Value("${seed.admin.email}")
    String email;

    @Override
    public void run(String... args) {


        if (userRepo.findByEmail(email).isEmpty()) {
            UserProfile admin = UserProfile.builder()
                    .firstName("Dev")
                    .lastName("admin")
                    .email(email)
                    .passwordHarsh(encoder.encode(adminPassword)).enabled(true)
                    .user_role(UserRole.ADMIN)
                    .build();
            userRepo.save(admin);
            System.out.println("Seeded admin: " + email);
        }
    }
}

