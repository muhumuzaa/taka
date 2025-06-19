package com.example.taka.services;

import com.example.taka.models.UserProfile;
import com.example.taka.models.UserRole;
import com.example.taka.repos.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CustomUserDetailsServiceTest {

    @Mock
    private UserProfileRepository repo;

    @InjectMocks
    private CustomUserDetailsService svc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadByUsernameSuccess() {
        // Arrange: build a UserProfile with ADMIN role
        UserProfile up = new UserProfile();
        up.setEmail("x@e.com");
        up.setPasswordHarsh("hashedpwd");
        up.setUser_role(UserRole.ADMIN);

        when(repo.findByEmail("x@e.com")).thenReturn(Optional.of(up));

        // Act
        UserDetails ud = svc.loadUserByUsername("x@e.com");

        // Assert
        assertEquals("x@e.com", ud.getUsername(), "username should match email");
        assertTrue(
                ud.getAuthorities()
                        .stream()
                        .anyMatch(a -> "ADMIN".equals(a.getAuthority())),
                "should have ADMIN authority"
        );
    }

    @Test
    void loadByUsernameNotFound() {
        when(repo.findByEmail("nope@x.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> svc.loadUserByUsername("nope@x.com"),
                "should throw when user not found"
        );
    }
}
