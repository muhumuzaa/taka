package com.example.taka.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ListingDtos {

    /*what client sends for Request*/
    public static record CreateRequestDto(
            /*
             * Name comes from authenticated user
             @NotBlank(message = "Name is required")
             @Size(max = 100, message = "Name must be at most 100 characters")
             String requesterName,
             */
            @NotBlank(message = "Title is required")
            @Size(max = 100, message = "Title must be at most 100 characters")
            String title,

            @NotBlank(message = "Description is required")
            @Size(max = 500, message = "Description must be at most 500 characters")
            String description,

            @Size(max = 255, message = "Image URL must be at most 255 characters")
            String imageUrl,

            @NotNull(message ="Offer price is required")
            @DecimalMin(value = "0.0", inclusive=false, message="Offer price must be greater than 0")
            BigDecimal offerPrice,

            @Size(max = 100, message = "Category must be at most 100 characters")
            String category,

            @NotBlank(message = "Location is required")
            @Size(max = 100, message = "Location must be at most 100 characters")
            String location
    ){}

    /* what the server sends back for Request*/

    public static record ResponseToRequestDto(
            Long id,
            String title,
            String description,
            String imageUrl,
            BigDecimal offerPrice,
            String category,
            String location,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String status,
            String contentStatus,
            String ownerEmail
    ){}


    /*
    *
    *
    *
    * FOR REPLY
    *
    *
    *
    * */




    /*what client sends to create a reply*/
    public static record CreateReplyDto(
           /*

            * Name comes from authentication
            *
            @NotBlank(message = "Name is required")
            @Size(max = 100, message = "Name must be at most 100 characters")
            String responderName,
            */
            @NotBlank(message = "message is required")
            @Size(max = 500, message = "message must be at most 500 characters")
            String message,

            @Size(max = 255, message = "Image URL must be at most 255 characters")
            String imageUrl,

            @NotNull(message = "price is required")
            @DecimalMin(value="0.0", inclusive=false, message = "price must be greater than 0")
            @Digits(integer=10, fraction =1, message ="Price must be a valid monetary amount")
            BigDecimal price
    ){}

    /*what server returns back for a Reply*/
    public static record ResponseToReplyDto(
            Long id,
            String message,
            String imageUrl,
            BigDecimal price,
            LocalDateTime createdAt,
            String replierEmail,
            String contentStatus
    ){}
}
