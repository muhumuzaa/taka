package com.example.taka.services;

import com.example.taka.dto.ListingDtos;
import com.example.taka.models.Request;
import com.example.taka.models.RequestStatus;
import com.example.taka.models.UserProfile;
import com.example.taka.repos.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ListingServiceTest {

    @Mock
    private RequestRepository requestRepo;

    @InjectMocks
    private ListingService listingService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    //This method is expected to convert the DTO into a Request entity.
    @Test
    void whenFromDto_thenEntityHasMatchingFields(){
        // 1. Arrange: build a sample CreateRequestDto
        ListingDtos.CreateRequestDto dto = new ListingDtos.CreateRequestDto(
                "My Title",
                "My Description",
                "http://img.png",
                new BigDecimal("50.0"),
                "Electronics",
                "Toronto"


        );

        UserProfile owner = new UserProfile();
        owner.setEmail("black@black.com");

        Request entity = listingService.fromRequestDto(dto, owner);

        assertThat(entity.getTitle()).isEqualTo("My Title");
        assertThat(entity.getDescription()).isEqualTo("My Description");
        assertThat(entity.getOwner()).isEqualTo(owner);
        assertThat(entity.getImageUrl()).isEqualTo("http://img.png");
        assertThat(entity.getOfferPrice()).isEqualTo(new BigDecimal("50.0"));
        assertThat(entity.getCategory()).isEqualTo("Electronics");
        assertThat(entity.getLocation()).isEqualTo("Toronto");
        assertThat(entity.getStatus()).isEqualTo(RequestStatus.OPEN);
        assertThat(entity.getCreatedAt()).isNotNull();
        // Note: updatedAt is set to now in the entity’s field initializer

    }

    /**
     * Tests the `findAllRequests` method of the ListingService.
     * Verifies that the `findAll` method of the `RequestRepository` is called
     * and that a `Page` of `ResponseToRequestDto` is returned.
     */
    @Test
    void whenFindAll_thenRepoCalledAndPageReturned(){
        //a Pageable object for pagination and sorting.
        Pageable pageable = PageRequest.of(0,5, Sort.by("createdAt").descending());

        // Create a sample Request object to be returned by the mock repository.
        Request sample = new Request();
        sample.setId(1L);
        sample.setTitle("T1");

        //simulate the page data returned by the repository.
        Page<Request> dummyPage = new PageImpl<>(List.of(sample), pageable, 1);

        // Configure the mock requestRepo to return the dummyPage when findAll is called with the specified pageable.
        when(requestRepo.findAll(pageable)).thenReturn(dummyPage);


        Page<ListingDtos.ResponseToRequestDto> result = listingService.findAllRequests(pageable);


        assertThat(result.getTotalElements()).isEqualTo(1);

        // Verify that the findAll method of the requestRepo was called exactly once with the given pageable.
        verify(requestRepo, times(1)).findAll(pageable);
    }

    @Test
    void whenUpdateRequest_thenRepoSaveCalled(){

        Long id = 1L;

        Request existing = new Request();
        existing.setId(id);
        existing.setTitle("Old Title");
        existing.setDescription("Old Desc");

        //mock requestRepo to return an Optional containing the existing request
        when(requestRepo.findById(id)).thenReturn(Optional.of(existing));

        //Configure the mock requestRepo to return the same Request object that was passed to its save method.
        // This simulates the behavior of a real repository that saves and returns the entity.
        when(requestRepo.save(any(Request.class))).thenAnswer(invocation ->invocation.getArgument(0));

        //creating a CreateRequestDto with updated info
        var dto = new ListingDtos.CreateRequestDto(
                "New Title",
                "New Desc",
                null,
                new BigDecimal("20.00"),
                "Books",
                "Toronto"
                );

        //Calling the updateRequest method on the listingService with the ID and the updated DTO.
        Request updated = listingService.updateRequest(id, dto);


        assertThat(updated.getTitle()).isEqualTo("New Title");
        assertThat(updated.getDescription()).isEqualTo("New Desc");
        assertThat(updated.getOfferPrice()).isEqualTo(new BigDecimal("20.00"));
        assertThat(updated.getCategory()).isEqualTo("Books");
        assertThat(updated.getLocation()).isEqualTo("Toronto");

        // Verify that the findById method of the requestRepo was called with the specified ID.
        verify(requestRepo, times(1)).findById(1L);

        // Verify that the save method of the requestRepo was called with any Request object.
        verify(requestRepo, times(1)).save(any(Request.class));
    }
}
