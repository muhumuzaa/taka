package com.example.taka.controllers;


import com.example.taka.dto.ListingDtos;
import com.example.taka.models.Request;
import com.example.taka.models.Reply;
import com.example.taka.models.UserProfile;
import com.example.taka.models.UserRole;
import com.example.taka.repos.ReplyRepository;
import com.example.taka.services.ListingService;
import com.example.taka.services.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;
    private final ReplyRepository replyRepo;
    private final UserProfileService userProfService;


   /**
    * FOR REQUESTS
    * */

    /* 1. Get /api/requests ->  Get all buy requests with pagination and sorting*/
    @GetMapping
    public Page<ListingDtos.ResponseToRequestDto> getAllRequests(Pageable pageable){
        return listingService.findAllRequests(pageable);
    }

    /* 2. Get /api/requests/{id}  -> Get a single request*/
    @GetMapping("/{id}")
    public ListingDtos.ResponseToRequestDto getSingleRequest(@PathVariable Long id){
        Request r = listingService.findRequestById(id);
        return listingService.toRequestDto((r));
    }

    /* 3. POST /api/requests -> create a new buy request*/
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListingDtos.ResponseToRequestDto createRequest(@RequestBody @Valid ListingDtos.CreateRequestDto dto, Principal principal){

        //first look up authenticated user
        UserProfile owner = userProfService.findByEmail(principal.getName());

        //convert Dto -> Entity
        Request request = listingService.fromRequestDto(dto, owner);

        //save entity
        Request saved = listingService.saveRequest(request);
        //back to Dto and return
        return listingService.toRequestDto(saved);
    }

    /* 4. PUT  -> Update Request*/
    @PutMapping("/{id}")
    public ListingDtos.ResponseToRequestDto updateRequest(@PathVariable Long id, @RequestBody @Valid ListingDtos.CreateRequestDto dto, Principal principal){

        //find Request
        Request existing = listingService.findRequestById(id);

        UserProfile owner = userProfService.findByEmail(principal.getName());

        //Only owner can update
        if(!existing.getOwner().getEmail().equals(owner.getEmail())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorize to make this update");
        }

        //save entity
        Request updated = listingService.updateRequest(id, dto);
        //return Dto of the updated row
        return listingService.toRequestDto(updated);
    }

    /* 5. Delete  -> Delete Request*/
    @DeleteMapping("/{id}")
    public void deleteRequest(@PathVariable Long id, Principal principal){

        Request existing = listingService.findRequestById(id);

        UserProfile owner = userProfService.findByEmail(principal.getName());

        boolean isOwner = existing.getOwner().getEmail().equals(owner.getEmail());
        boolean isAdmin = owner.getUser_role() == UserRole.ADMIN;
        if(!isOwner && !isAdmin){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to make this action.");
        }
        listingService.deleteRequest(id);
    }


    /**
     * FOR REPLIES
     * */
    /*
    * 1. Get replies for a particular request
    * GET /api/requests/{id}/replies
    * */
    @GetMapping("/{id}/replies")
    public List<ListingDtos.ResponseToReplyDto> getReplies(@PathVariable Long id){
        //fetch the parent Request(which loads its replies)
        return listingService.getAllRepliesToRequest(id);

    }

    /*
    * 2. Add reply to a request
    * POST /api/requests/{id}/replies
    * */
    @PostMapping("/{id}/replies")
    public ListingDtos.ResponseToReplyDto addReply(@PathVariable @RequestBody @Valid Long id, ListingDtos.CreateReplyDto dto, Principal principal){


        //identify the calling user
        UserProfile replier = userProfService.findByEmail(principal.getName());
        if(!replier.isEnabled()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is not yet verified");
        }

        //find the request
        Request request = listingService.findRequestById(id);
        //map Dto to Reply entity (sets parent Request internally)
        Reply reply = listingService.fromReplyDto(request, dto, replier);
        //save to db
        Reply saved = listingService.saveReply(reply);
        //Map entity to Dto and return
        return listingService.toReplyDto(saved);
    }

    /**
    *  SORTING AND SEARCHING REQUESTS
    **/

    /*
    * 1. Search Requests By title
    * Returns a list of RequestResponseDto
    * */
    @GetMapping("/search/title")
    public List<ListingDtos.ResponseToRequestDto> searchByTitle(@RequestParam("title") String title){
        //fetch matching entities

        return listingService.findRequestByTitle(title);
    }

    //find all replies by a user
    @GetMapping("/replies/user")
    public List<ListingDtos.ResponseToReplyDto> getAllRepliesForUser( Principal principal){
        String email = principal.getName();

        return listingService.getAllUserReplies(email);
    }

}
