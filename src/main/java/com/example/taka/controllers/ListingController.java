package com.example.taka.controllers;


import com.example.taka.dto.ListingDtos;
import com.example.taka.models.Request;
import com.example.taka.models.Reply;
import com.example.taka.repos.ReplyRepository;
import com.example.taka.services.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;
    private final ReplyRepository replyRepo;

   /**
    * FOR REQUESTS
    * */

    /* 1. Get /api/requests ->  Get all buy requests with pagination and sorting*/
    @GetMapping
    public Page<ListingDtos.ResponseToRequestDto> getAllRequests(Pageable pageable){
        return listingService.findAll(pageable).map(listingService::toDto);
    }

    /* 2. Get /api/requests/{id}  -> Get a single request*/
    @GetMapping("/{id}")
    public ListingDtos.ResponseToRequestDto getSingleRequest(@PathVariable Long id){
        Request r = listingService.findById(id);
        return listingService.toDto((r));
    }

    /* 3. POST /api/requests -> create a new buy request*/
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListingDtos.ResponseToRequestDto createRequest(@RequestBody @Valid ListingDtos.CreateRequestDto dto){
        //Dto -> Entity
        Request toSave = listingService.fromDto(dto);
        //save entity
        Request saved = listingService.save(toSave);
        //back to Dto and return
        return listingService.toDto(saved);
    }

    /* 4. PUT  -> Update Request*/
    @PutMapping("/{id}")
    public ListingDtos.ResponseToRequestDto updateRequest(@PathVariable Long id, @RequestBody @Valid ListingDtos.CreateRequestDto dto){
        //DTO -> entity
        Request updatedEntity = listingService.fromDto(dto);
        //copy id into new entity so update used correct row
        updatedEntity.setId(id);
        //save entity
        Request saved = listingService.updateRequest(id, updatedEntity);
        //return Dto of the updated row
        return listingService.toDto(saved);
    }

    /* 5. Delete  -> Delete Request*/
    @DeleteMapping("/{id}")
    public void deleteRequest(@PathVariable Long id){
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
        Request r = listingService.findById(id);

        //map each reply ->toDto
        return r.getReplies().stream().map(listingService::toDto).toList();
    }

    /*
    * 2. Add reply to a request
    * POST /api/requests/{id}/replies
    * */
    @PostMapping("/{id}/replies")
    public ListingDtos.ResponseToReplyDto addReply(@PathVariable Long id, @RequestBody @Valid ListingDtos.CreateReplyDto dto){
        Request request = listingService.findById(id);
        //map Dto to Reply entity (sets parent Request internally)
        Reply toSave = listingService.fromDto(id, dto);
        //save to db
        Reply saved = listingService.saveReply(toSave);
        //Map entity to Dto and return
        return listingService.toDto(saved);
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
        List<Request> matches = listingService.requestByTitle(title);
        return matches.stream().map(listingService::toDto).toList();
    }

}
