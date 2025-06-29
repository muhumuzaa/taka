package com.example.taka.controllers;

import com.example.taka.models.UserProfile;
import com.example.taka.repos.UserProfileRepository;
import com.example.taka.security.JwtUtil;
import com.example.taka.services.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

   @Autowired
   MockMvc mvc;

   @MockitoBean
   AuthenticationManager authManager;

   @MockitoBean
   JwtUtil jwtUtil;

   @MockitoBean
   UserProfileRepository userRepo;

   @MockitoBean
   PasswordEncoder encoder;

   @MockitoBean
   CustomUserDetailsService customUserDetailsService;

   @Test
   void loginSuccess() throws Exception {
      String email = "as@hd.com", pass = "pwd";
      when(jwtUtil.generateToken(email)).thenReturn("fake.token");

      mvc.perform(post("/api/auth/login")
                      .contentType(APPLICATION_JSON)
                      .content("{\"email\":\"" + email + "\",\"password\":\"" + pass + "\"}"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.token").value("fake.token"));
   }

   @Test
   void loginFail() throws Exception {
      doThrow(new BadCredentialsException("")).when(authManager).authenticate(any());

      mvc.perform(post("/api/auth/login")
                      .contentType(APPLICATION_JSON)
                      .content("{\"email\":\"xhd@g.com\",\"password\":\"y\"}"))
              .andExpect(status().isUnauthorized());
   }

   @Test
   void registerSuccess() throws Exception {
      // simulate “no existing user”
      when(userRepo.findByEmail(any())).thenReturn(Optional.empty());

      String json = """
            {
              "firstName":"Alice",
              "lastName":"Smith",
              "email":"alice@example.com",
              "password":"Secret123"
            }
            """;

      mvc.perform(post("/api/auth/register")
                      .contentType(APPLICATION_JSON)
                      .content(json))
              .andExpect(status().isCreated())
              .andExpect(content().string("Account registered successfully"));
   }

   @Test
   void registerConflict() throws Exception {
      // simulate duplicate email
      when(userRepo.findByEmail("dup@a.com"))
              .thenReturn(Optional.of(new UserProfile()));

      String json = """
            {
              "firstName":"John",
              "lastName":"Doe",
              "email":"dup@a.com",
              "password":"anything"
            }
            """;

      mvc.perform(post("/api/auth/register")
                      .contentType(APPLICATION_JSON)
                      .content(json))
              .andExpect(status().isConflict());
   }
}
