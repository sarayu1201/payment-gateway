package com.gateway.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @Column(length = 64)
    private String id;
    
    @Column(columnDefinition = "UUID")
    private UUID merchantId;
    
    @Column(nullable = false)
    private Integer amount;
    
    @Column(length = 3)
    private String currency = "INR";
    
    @Column(length = 255)
    private String receipt;
    
    @Column(columnDefinition = "jsonb")
    private String notes;
    
    @Column(length = 20)
    private String status = "created";
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
