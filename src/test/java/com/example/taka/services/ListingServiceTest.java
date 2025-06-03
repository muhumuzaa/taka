package com.example.taka.services;

import com.example.taka.dto.ListingDtos;
import com.example.taka.models.Request;
import com.example.taka.models.RequestStatus;
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

    @Test
    void whenFromDto_thenEntityHasMatchingFields(){
        // 1. Arrange: build a sample CreateRequestDto
        ListingDtos.CreateRequestDto dto = new ListingDtos.CreateRequestDto(
                "My Title",
                "My Description",
                "Alice",
                "http://img.png",
                new BigDecimal("50.0"),
                "Electronics",
                "Toronto"
        );

        //2. call fromDto() which produces a Request entity
        Request entity = listingService.fromDto(dto);

        assertThat(entity.getTitle()).isEqualTo("My Title");
        assertThat(entity.getDescription()).isEqualTo("My Description");
        assertThat(entity.getRequesterName()).isEqualTo("Alice");
        assertThat(entity.getImageUrl()).isEqualTo("http://img.png");
        assertThat(entity.getOfferPrice()).isEqualTo(new BigDecimal("50.0"));
        assertThat(entity.getCategory()).isEqualTo("Electronics");
        assertThat(entity.getLocation()).isEqualTo("Toronto");
        assertThat(entity.getStatus()).isEqualTo(RequestStatus.OPEN);
        assertThat(entity.getCreatedAt()).isNotNull();
        // Note: updatedAt is set to now in the entity’s field initializer

    }

    @Test
    void whenFindAll_thenRepoCalledAndPageReturned(){
        Pageable pageable = PageRequest.of(0,5, Sort.by("createdAt").descending());
        List<Request> dummyList = List.of(new Request(1L, "T1", "D1", "A", null, null, null, null, LocalDateTime.now(), LocalDateTime.now(), RequestStatus.OPEN, new ArrayList<>()));
        Page<Request> dummyPage = new PageImpl<>(dummyList, pageable, 1);
        when(requestRepo.findAll(pageable)).thenReturn(dummyPage);
        Page<Request> result = listingService.findAll(pageable);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(requestRepo, times(1)).findAll(pageable);
    }

    @Test
    void whenUpdateRequest_thenRepositorySaveCalled(){
        Request existing = new Request(1L, "Old Title", "Old Desc", "Bob", null, null, null, null,
                LocalDateTime.now(), LocalDateTime.now(), RequestStatus.OPEN, new ArrayList<>());

        ListingDtos.CreateRequestDto dto = new ListingDtos.CreateRequestDto( "New Title", "New Desc", "Bob",
                null, new BigDecimal("20.00"), "Books", "Vancouver");

        //Simulate findBy
        when(requestRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(requestRepo.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Request updatedEntity = listingService.fromDto(dto);
        updatedEntity.setId(1L);
        Request saved = listingService.updateRequest(1L, updatedEntity);

        assertThat(saved.getTitle()).isEqualTo("New Title");
        assertThat(saved.getDescription()).isEqualTo("New Desc");
        assertThat(saved.getOfferPrice()).isEqualTo(new BigDecimal("20.00"));

        verify(requestRepo, times(1)).findById(1L);
        verify(requestRepo, times(1)).save(any(Request.class));
    }
}
