package com.example.taka.services;

import com.example.taka.models.UserProfile;
import com.example.taka.repos.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserProfileRepository userProfileRepo;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        String normalized = email.trim().toLowerCase();
        UserProfile user = userProfileRepo.findByEmail(normalized).orElseThrow(() -> new UsernameNotFoundException("User not found: "+normalized));

        //convert userRole into grantedAuthority
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getUser_role().name());

        //return SpringSecurity userDetails object containing;
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHarsh(),
                Collections.singleton(authority)
        );
    }

}
