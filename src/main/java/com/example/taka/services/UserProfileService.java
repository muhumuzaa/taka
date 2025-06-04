package com.example.taka.services;

import com.example.taka.models.UserProfile;
import com.example.taka.repos.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepo;

    public Page<UserProfile> findAll(Pageable pageable){
        return userProfileRepo.findAll(pageable);
    }

    public UserProfile findById(Long id){
        return userProfileRepo.findById(id).orElseThrow(() -> new RuntimeException("User "+ id +" not found "));
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

}
