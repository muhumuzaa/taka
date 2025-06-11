package com.example.taka.controllers;

import com.example.taka.dto.ListingDtos.CreateRequestDto;
import com.example.taka.dto.ListingDtos.ResponseToRequestDto;
import com.example.taka.models.Request;
import com.example.taka.models.RequestStatus;
import com.example.taka.models.UserProfile;
import com.example.taka.repos.ReplyRepository;
import com.example.taka.services.ListingService;
import com.example.taka.services.UserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ListingController.class)
class ListingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListingService listingService;   // mock the service layer

    @MockitoBean
    private ReplyRepository replyRepository; // mock the reply repository

    @MockitoBean
    private UserProfileService userProfileService;

    //autowired objectMapper to convert java objects to/from json. Serialization
    @Autowired
    private ObjectMapper objectMapper;

    //to test POST api/requests when a request DTO is sent. Expects 201 status
    @Test
    void whenPostValidRequest_thenReturns201AndCorrectJson() throws Exception {
        // Arrange: create a DTO that client would send
        CreateRequestDto dto = new CreateRequestDto(
                "New Bike",
                "Almost new bike",
                "http://img.png",
                new BigDecimal("60.0"),
                "Autos",
                "Ottawa"

        );

        //email for owner
        String email ="owner@email.com";

        //create owner object
        UserProfile owner = new UserProfile();
        owner.setEmail(email);

        //configure userProfileService mock to return the owner userProfile
        when(userProfileService.findByEmail(email)).thenReturn(owner);

        //stub service mapping &save
        //a request object to simulate

        // Prepare a dummy Request entity that the service will return
        Request savedEntity = Request.builder()
                .id(1L)
                .title("New Bike")
                .description("Almost new bike")
                .requesterName("Alice")
                .imageUrl("http://img.png")
                .offerPrice(new BigDecimal("100.00"))
                .category("Vehicles")
                .location("Ottawa")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(RequestStatus.OPEN)
                .build();

        // Prepare the DTO that the controller should return
        ResponseToRequestDto responseDto = new ResponseToRequestDto(
                savedEntity.getId(),
                savedEntity.getTitle(),
                savedEntity.getDescription(),
                savedEntity.getRequesterName(),
                savedEntity.getImageUrl(),
                savedEntity.getOfferPrice(),
                savedEntity.getCategory(),
                savedEntity.getLocation(),
                savedEntity.getCreatedAt(),
                savedEntity.getUpdatedAt(),
                savedEntity.getStatus().name(),
                List.of() // no replies
        );

        // Stub the service methods
        when(listingService.fromDto(any(CreateRequestDto.class)))
                .thenReturn(Request.builder()
                        .title(dto.title())
                        .description(dto.description())
                        .requesterName(dto.requesterName())
                        .imageUrl(dto.imageUrl())
                        .offerPrice(dto.offerPrice())
                        .category(dto.category())
                        .location(dto.location())
                        .build()
                );
        when(listingService.save(any(Request.class))).thenReturn(savedEntity);
        when(listingService.toDto(savedEntity)).thenReturn(responseDto);

        // Act & Assert: perform POST /api/requests and verify response
        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Bike"))
                .andExpect(jsonPath("$.requesterName").value("Alice"));
    }

    @Test
    void whenGetPagedRequests_thenReturnsJsonPage() throws Exception {
        // Arrange: create a sample Request entity
        Request r = Request.builder()
                .id(1L)
                .title("Sample")
                .description("Desc")
                .requesterName("Bob")
                .imageUrl(null)
                .offerPrice(null)
                .category(null)
                .location(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(RequestStatus.OPEN)
                .build();

        // Wrap it in a Page object
        Page<Request> page = new PageImpl<>(List.of(r), PageRequest.of(0, 20), 1);

        // Prepare the DTO that the controller should return
        ResponseToRequestDto dto = new ResponseToRequestDto(
                r.getId(),
                r.getTitle(),
                r.getDescription(),
                r.getRequesterName(),
                r.getImageUrl(),
                r.getOfferPrice(),
                r.getCategory(),
                r.getLocation(),
                r.getCreatedAt(),
                r.getUpdatedAt(),
                r.getStatus().name(),
                List.of()
        );

        // Stub the service methods
        when(listingService.findAll(any(Pageable.class))).thenReturn(page);
        when(listingService.toDto(r)).thenReturn(dto);

        // Act & Assert: perform GET /api/requests?page=0&size=20 and verify JSON
        mockMvc.perform(get("/api/requests")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Sample"));
    }
}
