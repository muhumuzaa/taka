package com.example.taka.services;

import com.example.taka.dto.ListingDtos;
import com.example.taka.models.Reply;
import com.example.taka.models.Request;
import com.example.taka.repos.RequestRepository;
import com.example.taka.repos.ReplyRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ListingService {
    private final RequestRepository requestRepo;
    private final ReplyRepository replyRepo;

    //all request pageable
    public Page<Request> findAll(Pageable pageable) {
        return requestRepo.findAll(pageable);
    }

    //request by Id
    public Request findById(Long id){
        return requestRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found: "+id));
    }

    //requests by title
    public List<Request> requestByTitle(String title){
        return requestRepo.findByTitle(title);
    }

    //save request
    public Request save(Request lr){
        return requestRepo.save(lr);
    }

    //update request
    public Request updateRequest(Long id, Request update){
        Request existing = findById(id);
        existing.setTitle(update.getTitle());
        existing.setDescription(update.getDescription());
        existing.setRequesterName(update.getRequesterName());
        existing.setImageUrl(update.getImageUrl());
        existing.setOfferPrice(update.getOfferPrice());
        existing.setLocation(update.getLocation());
        existing.setUpdatedAt(java.time.LocalDateTime.now());

        return requestRepo.save(existing);
    }

    //delete request
    public void deleteRequest(Long id){
        requestRepo.deleteById(id);
    }



    // -------------------Mapping DTO -> Entity ------------------------------------

    /**
            * Convert a CreateRequestDto (validated at the controller boundary) into a Request entity.
            * Uses Lombok's @Builder on the Request class for brevity.
            */
    public Request fromDto(ListingDtos.CreateRequestDto dto){
        return Request.builder()
                .title(dto.title())
                .description(dto.description())
                .requesterName(dto.requesterName())
                .imageUrl(dto.imageUrl())
                .offerPrice(dto.offerPrice())
                .category(dto.category())
                .location(dto.location())
                .build();
    }

    /**
     * Converting a Request entity (with replies loaded) into a Response DTO.
     * This is what controller returns to the client.
     */
    public ListingDtos.ResponseToRequestDto toDto(Request r){
        List<ListingDtos.ResponseToReplyDto> replyDtos = r.getReplies().stream().map(this::toDto).toList();

        return new ListingDtos.ResponseToRequestDto(
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
                replyDtos
        );
    }




    // ------------CRUD for Reply ----------------------------------------------

    /* Save a new or updated reply*/
    public Reply saveReply(Reply reply){
        return replyRepo.save(reply);
    }

    /*Delete a reply by id*/
    public void deleteReply(Long id){
        replyRepo.deleteById(id);
    }


    // ------------DTO -> Entity mapping for Reply -----------------------------

    /** Convert a CreateReplyDto (validated at the controller boundary) into a reply entity
    * Fetching the parent Request first so I can set the relationship
    * */
    public Reply fromDto(Long requestId, ListingDtos.CreateReplyDto dto){
        Request parent = findById(requestId);
        return Reply.builder()
                .responderName(dto.responderName())
                .message(dto.message())
                .imageUrl(dto.imageUrl())
                .price(dto.price())
                .request(parent)
                .build();
    }

    /**
     * Convert a Reply entity into a Response Dto
    * */
    public ListingDtos.ResponseToReplyDto toDto(Reply r){
        return new ListingDtos.ResponseToReplyDto(
                r.getId(),
                r.getResponderName(),
                r.getMessage(),
                r.getImageUrl(),
                r.getPrice(),
                r.getCreatedAt()
        );
    }
}
