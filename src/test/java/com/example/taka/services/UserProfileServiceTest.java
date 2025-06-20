package com.example.taka.services;

import com.example.taka.dto.UserProfileDtos;
import com.example.taka.repos.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserProfileServiceTest {
    @Mock
    UserProfileRepository userRepo;
    @Mock
    PasswordEncoder encoder;
    @InjectMocks UserProfileService userProfService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);}


    @Test
    void fromDto_and_toDto_roundTrip(){
        //arrange: build a CreateUserProfileDto
        var dto = new UserProfileDtos.CreateUserProfileDto("Alice", "Smith", "ab@a.com", "secret", "bio", "img.png","54454554");
        when(encoder.encode("secret")).thenReturn("hashed");

        //Act: map to entity and back
        var entity = userProfService.fromDto(dto);
        var outDto = userProfService.toDto(entity);

        //Assert
        assertEquals("Alice", entity.getFName());
        assertEquals("hashed", entity.getPasswordHarsh());
        assertEquals(entity.getEmail(), outDto.email());
        assertFalse(outDto.enabled());
    }





}