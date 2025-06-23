package com.example.taka.repos;

import com.example.taka.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    List<UserProfile> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String fName, String lName);

    Optional<UserProfile> findByEmail(String email);

}
