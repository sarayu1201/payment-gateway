package com.gateway.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "merchants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Merchant {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    
    @Column(length = 255)
    private String name;
    
    @Column(length = 255, unique = true)
    private String email;
    
    @Column(length = 64, unique = true)
    private String apiKey;
    
    @Column(length = 64)
    private String apiSecret;
    
    @Column(columnDefinition = "TEXT")
    private String webhookUrl;
    
    @Column(columnDefinition = "boolean default true")
    private Boolean isActive = true;
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
