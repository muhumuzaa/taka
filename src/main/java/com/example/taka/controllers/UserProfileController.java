package com.example.taka.controllers;

import com.example.taka.dto.UserProfileDtos;
import com.example.taka.models.UserProfile;
import com.example.taka.services.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserProfileController {
    private final UserProfileService userProfService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserProfileDtos.UserProfileResponseDto createUser(@Valid @RequestBody UserProfileDtos.CreateUserProfileDto dto){


        UserProfile entity = new UserProfile();

        //map Dto to Entity
        entity.setFName(dto.fName());
        entity.setLName(dto.lName());
        entity.setEmail(dto.email().trim().toLowerCase());
        entity.setPasswordHarsh(passwordEncoder.encode(dto.password()));
        entity.setBio(dto.bio());
        entity.setProfileImage(dto.profileImage());
        entity.setPhoneNumber(dto.phoneNumber());
        //other defaults from UserProfile entity automatically set

        UserProfile newProf = userProfService.createUser(entity);

        //return the response. Map Entity to Dto
        return new UserProfileDtos.UserProfileResponseDto(
                newProf.getId(),
                newProf.getFName(),
                newProf.getLName(),
                newProf.getEmail(),
                newProf.getBio(),
                newProf.getProfileImage(),
                newProf.getPhoneNumber(),
                newProf.isEnabled(),
                newProf.getCreatedAt(),
                newProf.getUpdatedAt(),
                newProf.getUser_role().name()
        );
    }

    @GetMapping("/{id}")
    public UserProfileDtos.UserProfileResponseDto getUserById(@PathVariable Long id){
        //entity to Dto mapping
        UserProfile user = userProfService.findById(id);

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

    @GetMapping
    public Page<UserProfileDtos.UserProfileResponseDto> getAllUser(Pageable pageable){
        return userProfService.findAll(pageable).map();
    }

}
