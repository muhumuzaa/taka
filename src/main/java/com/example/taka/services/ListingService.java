package com.example.taka.services;

import com.example.taka.dto.ListingDtos;
import com.example.taka.models.Reply;
import com.example.taka.models.Request;
import com.example.taka.models.UserProfile;
import com.example.taka.repos.RequestRepository;
import com.example.taka.repos.ReplyRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListingService {
    private final RequestRepository requestRepo;
    private final ReplyRepository replyRepo;
    private final UserProfileService userService;


    // -------------------Mapping DTO -> Entity ------------------------------------
    //convert CreateRequestDto -> Request entity & set owner
    public Request fromRequestDto(ListingDtos.CreateRequestDto dto, UserProfile owner){
        return Request.builder()
                .title(dto.title())
                .description(dto.description())
                .owner(owner)
                .imageUrl(dto.imageUrl())
                .offerPrice(dto.offerPrice())
                .category(dto.category())
                .location(dto.location())
                .build();
    }

    /*
     * Converting a Request entity (with replies loaded) into a Response DTO.
     * This is what controller returns to the client.
     */
    public ListingDtos.ResponseToRequestDto toRequestDto(Request request){
        return new ListingDtos.ResponseToRequestDto(
                request.getId(),
                request.getTitle(),
                request.getDescription(),
                request.getImageUrl(),
                request.getOfferPrice(),
                request.getCategory(),
                request.getLocation(),
                request.getCreatedAt(),
                request.getUpdatedAt(),
                request.getStatus().name(),
                request.getContentStatus().name(),
                request.getOwner().getEmail()

        );
    }

    // ------------CRUD for Request ----------------------------------------------

    public Page<ListingDtos.ResponseToRequestDto> findAllRequests(Pageable pageable){
        return requestRepo.findAll(pageable).map(this::toRequestDto);
    }

    public Request findRequestById(Long id){
        return requestRepo.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Request not found: "+id
        ));
    }


    public Request saveRequest(Request request){
        return requestRepo.save(request);
    }

    //update request with new fields from Dto
    public Request updateRequest(Long id, ListingDtos.CreateRequestDto dto){
        Request existing = findRequestById(id);

        existing.setTitle(dto.title());
        existing.setDescription(dto.description());
        existing.setImageUrl(dto.imageUrl());
        existing.setOfferPrice(dto.offerPrice());
        existing.setCategory(dto.category());
        existing.setLocation(dto.location());
        existing.setUpdatedAt(LocalDateTime.now());
        //owner, createdAt, status, remain unchanged.
        return requestRepo.save(existing);
    }


    public void deleteRequest(Long id){
        if(!requestRepo.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found: "+id);
        };
        requestRepo.deleteById(id);
    }


    public List<ListingDtos.ResponseToRequestDto> findRequestByTitle(String title){
        return requestRepo.findByTitle(title).stream().map(this::toRequestDto).toList();
    }






    // ------------CRUD for Reply ----------------------------------------------


    // ------------DTO -> Entity mapping for Reply -----------------------------

    /** Convert a CreateReplyDto (validated at the controller boundary) into a reply entity
     * Fetching the parent Request first so I can set the relationship
     * */
    public Reply fromReplyDto(Request parentRequest, ListingDtos.CreateReplyDto dto, UserProfile replier){
        return Reply.builder()
                .message(dto.message())
                .imageUrl(dto.imageUrl())
                .price(dto.price())
                //tying this Reply to the parent Request
                .request(parentRequest)
                //the user who made the reply
                .replier(replier)

                .build();
    }

    /**
     * Convert a Reply entity into a Response Dto
     * */
    public ListingDtos.ResponseToReplyDto toReplyDto(Reply r){
        return new ListingDtos.ResponseToReplyDto(
                r.getId(),
                r.getMessage(),
                r.getImageUrl(),
                r.getPrice(),
                r.getCreatedAt(),
                r.getReplier().getEmail(),
                r.getContentStatus().name()
        );
    }

    //get replies for a request
    public List<ListingDtos.ResponseToReplyDto> getAllRepliesToRequest(Long requestId){
        Request request = findRequestById(requestId);
        return request.getReplies().stream().map(this::toReplyDto).toList();
    }

    //get all replies for a user
    public List<ListingDtos.ResponseToReplyDto> getAllUserReplies(String email){
        UserProfile replier = userService.findByEmail(email);
        return replyRepo.findByReplier(replier).stream().map(this::toReplyDto).toList();
    }

    //update a reply
    public Reply updateReply(Long id, ListingDtos.CreateReplyDto dto, String userEmail){
        Reply existing = replyRepo.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reply not found: "+id));

        UserProfile user = userService.findByEmail(userEmail);

        if(!existing.getReplier().getEmail().equals(user.getEmail())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot edit another user's reply");
        }
        existing.setMessage(dto.message());
        existing.setImageUrl(dto.imageUrl());
        existing.setPrice(dto.price());
        existing.setUpdatedAt(LocalDateTime.now());

        return replyRepo.save(existing);
    }


    /* Save a new or updated reply*/
    public Reply saveReply(Reply reply){
        return replyRepo.save(reply);
    }


    /*Delete a reply by id*/
    public void deleteReply(Long id, String userEmail){

        Reply reply = replyRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reply not found: "+id));

        UserProfile user = userService.findByEmail(userEmail);

        //check if the replier email and the logged in replier email are the same
        if(!reply.getReplier().getEmail().equals(user.getEmail())){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot delete another user's reply");
        }
        replyRepo.deleteById(id);
    }


    }
