package com.example.taka.services;

import com.example.taka.dto.UserProfileDtos;
import com.example.taka.models.UserProfile;
import com.example.taka.models.UserRole;
import com.example.taka.repos.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepo;
    private final PasswordEncoder passwordEncoder;

    public Page<UserProfile> findAll(Pageable pageable){
        return userProfileRepo.findAll(pageable);
    }

    public UserProfile findById(Long id){
        return userProfileRepo.findById(id).orElseThrow(() -> new RuntimeException("User "+ id +" not found "));
    }

    public UserProfile findByEmail(String email){
        return userProfileRepo.findByEmail(email).orElseThrow(()->new RuntimeException("User with email: "+email +" not found"));
    }

    public List<UserProfile> finderByfNameOrlName(String fName, String lName){
        return userProfileRepo.findByFNameContainingIgnoreCaseOrLNameContainingIgnoreCase(fName, lName);
    }

    public UserProfile createUser(UserProfile user ){
        return userProfileRepo.save(user);
    }

    public UserProfile update(Long id, UserProfile updatedProf){
        UserProfile existing = findById(id);
        existing.setFName(updatedProf.getFName());
        existing.setLName(updatedProf.getLName());
        existing.setBio(updatedProf.getBio());
        existing.setProfileImage(updatedProf.getProfileImage());
        existing.setPhoneNumber(updatedProf.getPhoneNumber());
        existing.setEnabled(updatedProf.isEnabled());
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setUser_role(updatedProf.getUser_role());

        return userProfileRepo.save(existing);
    }

    public void delete(Long id){
        userProfileRepo.deleteById(id);
    }


    // -------------Mapping DTO -> Entity ---------------------------------------

    //converting theDto to a UserProfile entity
    public UserProfile fromDto(UserProfileDtos.CreateUserProfileDto dto){
        return UserProfile.builder()
                .fName(dto.fName())
                .lName(dto.lName())
                .email(dto.email())
                .passwordHarsh(passwordEncoder.encode(dto.password()))
                .bio(dto.bio())
                .profileImage(dto.profileImage())
                .phoneNumber(dto.phoneNumber())
                .user_role(UserRole.USER)
                .enabled(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ----------------Mapping Entity -> Dto ---------------------------------
    //converting a UserProfile entity to Dto
    public UserProfileDtos.UserProfileResponseDto toDto(UserProfile user) {
        return new UserProfileDtos.UserProfileResponseDto(
                user.getId(),
                user.getFName(),
                user.getLName(),
                user.getEmail(),
                user.getBio(),
                user.getProfileImage(),
                user.getPhoneNumber(),
                user.isEnabled(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUser_role().name()
        );
    }
}
