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

    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name="owner_id", nullable =false)
    private UserProfile owner;

    @Size(max = 255, message = "Image URL cannot exceed 255 characters")
    private String imageUrl;

    private BigDecimal offerPrice;

    @Size(max = 100, message = "Category cannot exceed 100 characters")
    private String category;

    @NotBlank(message = "Location cannot be empty")
    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable=false)
    private ContentStatus contentStatus = ContentStatus.ALLOWED;

    @Builder.Default
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval=true)
    @JsonManagedReference
    private List<Reply> replies = new ArrayList<>();
}
