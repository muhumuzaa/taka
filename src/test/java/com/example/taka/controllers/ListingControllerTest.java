package com.example.taka.controllers;

import com.example.taka.dto.ListingDtos.CreateRequestDto;
import com.example.taka.dto.ListingDtos.ResponseToRequestDto;
import com.example.taka.models.Request;
import com.example.taka.models.RequestStatus;
import com.example.taka.models.UserProfile;
import com.example.taka.repos.ReplyRepository;
import com.example.taka.security.JwtUtil;
import com.example.taka.services.CustomUserDetailsService;
import com.example.taka.services.ListingService;
import com.example.taka.services.UserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ListingController.class)
@AutoConfigureMockMvc(addFilters = false)
class ListingControllerTest {

    //mocks for all security‐related dependencies so Spring can wire up the filter without you having to spin up your real security stack
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    //Others
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
        //a request object to simulate what ListingService would return after saving
        Request saved = new Request();
        saved.setId(1L);
        saved.setTitle(dto.title());
        saved.setDescription(dto.description());
        saved.setImageUrl(dto.imageUrl());
        saved.setOfferPrice(dto.offerPrice());
        saved.setCategory(dto.category());
        saved.setLocation(dto.location());

        //configure the mock listingService
        // 1. when fromRequestDto is called with any CreateRequestDto and the owner, return saved Request
        when(listingService.fromRequestToDto(any(CreateRequestDto.class), eq(owner))).thenReturn(saved);

        //2. when saveRequest is called with the saved Request object, return the saved Request object
        when(listingService.saveRequest(saved)).thenReturn(saved);


        //prepare the DTO that controller will return
        //create a ResponseToRequestDto that simulates what controller should return as json
        var respDto = new ResponseToRequestDto(
                1L,
                saved.getTitle(),
                saved.getDescription(),
                saved.getImageUrl(),
                saved.getOfferPrice(),
                saved.getCategory(),
                saved.getLocation(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                RequestStatus.OPEN.name(),
                "ACTIVE",
                email
        );

        //configure mock listingService to return the respDto when toRequestDto is called
        when(listingService.toRequestDto(saved)).thenReturn(respDto);


        // Act & Assert: perform POST /api/requests and verify response
        mockMvc.perform(post("/api/requests")
                        .principal((Principal) () ->email)
                        .contentType(MediaType.APPLICATION_JSON)
                        //set Request body by converting the DTO to json
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Bike"))
                .andExpect(jsonPath("$.ownerEmail").value(email));
    }

    /**
     * Testing the GET /api/requests endpoint for retrieving paged requests.
     * It expects a 200 OK status and a JSON page containing the request DTOs.
     */
    @Test
    void whenGetPagedRequests_thenReturnsJsonPage() throws Exception {
        //creating a Pageable object for the request
        Pageable pageReq = PageRequest.of(0, 20);

        // Arrange: create a sample Request entity
        Request req = new Request();
        req.setId(1L);
        req.setTitle("Sample");

        //simulate a service returning a page of DTOs
        //creating a ResponseToRequestDto to be included in the simulated page
        var dto = new ResponseToRequestDto(
                1L,
                "Sample",
                null,
                null,
                null,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                RequestStatus.OPEN.name(),
                "ACTIVE",
                "some@some.com"
        );

        //creating a PageImpl containing the single DTO
        //simulating the paged result from the service
        Page<ResponseToRequestDto> dtoPage = new PageImpl<>(List.of(dto), pageReq, 1);

        //configure mock listingService to return dtoPage when findAllRequests is called
        when(listingService.findAllRequests(any(Pageable.class))).thenReturn(dtoPage);

        // act & assert
        //perform GET to api/requests with page and size parameters
        mockMvc.perform(get("/api/requests").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                //first element in content should have id field with value 1 and title of "sample"
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Sample"));


    }
}
