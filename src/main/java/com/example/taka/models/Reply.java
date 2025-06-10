package com.example.taka.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name ="replier_id", nullable = false)
    private UserProfile replier;

    @NotBlank(message = "message is required")
    @Size(max = 500, message = "message must be at most 500 characters")
    private String message;

    @Size(max = 255, message = "Image URL must be at most 255 characters")
    private String imageUrl;

    @NotNull(message = "price is required")
    @DecimalMin(value="0.0", inclusive=false, message = "price must be greater than 0")
    @Digits(integer=6, fraction =1, message ="Price must be a valid monetary amount")
    private BigDecimal price;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private ContentStatus contentStatus = ContentStatus.ALLOWED;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable =false)
    @JsonBackReference
    private Request request;
}
