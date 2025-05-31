package com.example.taka.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotBlank(message = "Requester name cannot be empty")
    @Size(max = 100, message = "Requester name cannot exceed 100 characters")
    private String requesterName;

    @Size(max = 255, message = "Image URL cannot exceed 255 characters")
    private String imageUrl;

    private BigDecimal offerPrice;

    @Size(max = 100, message = "Category cannot exceed 100 characters")
    private String category;

    @NotBlank(message = "Location cannot be empty")
    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.OPEN;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Reply> replies = new ArrayList<>();
}
