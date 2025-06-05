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

    //Get all users
    @GetMapping
    public Page<UserProfileDtos.UserProfileResponseDto> getAllUser(Pageable pageable){
        return userProfService.findAll(pageable).map(userProfService::toDto);
    }

    //Get user by id
    @GetMapping("/{id}")
    public UserProfileDtos.UserProfileResponseDto findById(@PathVariable Long id){
        UserProfile userProf = userProfService.findById(id);
        return userProfService.toDto(userProf);
    }

    //create user
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserProfileDtos.UserProfileResponseDto createUser(@Valid @RequestBody UserProfileDtos.CreateUserProfileDto dto){

        //first convert created dto into entity
        UserProfile toSave = userProfService.fromDto(dto);

        //save the entity
        UserProfile savedEntity = userProfService.createUser(toSave);

        //convert entity back to dto and return to client
        return userProfService.toDto(savedEntity);
    }

    @PutMapping("/{id}")
    public UserProfileDtos.UserProfileResponseDto updateUser(@PathVariable @RequestBody Long id, UserProfileDtos.CreateUserProfileDto dto){
        //Convert the client Dto to entity
        UserProfile userToUpdate = userProfService.fromDto(dto);

        //copy id into new entity so JPA will update
        userToUpdate.setId(id);

        //update
        UserProfile updated = userProfService.update(id, userToUpdate);

        //return the Dto to client
        return userProfService.toDto(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id){
        userProfService.delete(id);
    }



}
