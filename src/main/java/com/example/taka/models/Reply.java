package com.example.taka.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String responderName;

    @NotBlank(message = "message is required")
    @Size(max = 500, message = "message must be at most 500 characters")
    private String message;

    @Size(max = 255, message = "Image URL must be at most 255 characters")
    private String imageUrl;

    @NotBlank(message = "price is required")
    @Size(max=10, message = "price cant be above 10 figures")
    private BigDecimal price;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "request_id")
    @JsonBackReference
    private Request request;
}
