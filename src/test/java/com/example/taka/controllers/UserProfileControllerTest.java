package com.example.taka.controllers;

import com.example.taka.dto.UserProfileDtos;
import com.example.taka.models.UserProfile;
import com.example.taka.security.JwtUtil;
import com.example.taka.services.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserProfileController.class)
@AutoConfigureMockMvc(addFilters=false)
public class UserProfileControllerTest {
    @Autowired
    MockMvc mvc;
    @MockitoBean
    UserProfileService userPS;

    @MockitoBean
    JwtUtil jwtUtil;

    @MockitoBean
    com.example.taka.services.CustomUserDetailsService customUserDetailsService;

    @Test
    void createAndFetch() throws Exception{
        var now = LocalDateTime.now();
        //stub create
        var resp = new UserProfileDtos.UserProfileResponseDto( 5L,"Al","Bl","a@b.com","bio","img.png","1237788",false,now,now,"USER");
        when(userPS.fromDto(any())).thenReturn(new UserProfile());
        when(userPS.createUser(any())).thenReturn(new UserProfile());
        when(userPS.toDto(any())).thenReturn(resp);

        //POST -> 201 with correct fields
        mvc.perform(post("/api/users").contentType(APPLICATION_JSON)
                        .content("""
              {
                "fName":"Alice",
                "lName":"Smith",
                "email":"a@b.com",
                "password":"secretpw",
                "bio":"",
                "profileImage":"",
                "phoneNumber":"1234567890"
              }
              """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.email").value("a@b.com"))
                .andExpect(jsonPath("$.fName").value("Al"));

        //GET ->200
        when(userPS.findById(5L)).thenReturn(new UserProfile());
        mvc.perform(get("/api/users/5")).andExpect(status().isOk());
    }
}
