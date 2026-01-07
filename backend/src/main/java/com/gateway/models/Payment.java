package com.gateway.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @Column(length = 64)
    private String id;
    
    @Column(length = 64)
    private String orderId;
    
    @Column(columnDefinition = "UUID")
    private UUID merchantId;
    
    @Column(nullable = false)
    private Integer amount;
    
    @Column(length = 3)
    private String currency = "INR";
    
    @Column(length = 20)
    private String method; // upi or card
    
    @Column(length = 20)
    private String status = "processing";
    
    @Column(length = 255)
    private String vpa; // For UPI
    
    @Column(length = 20)
    private String cardNetwork; // For cards
    
    @Column(length = 4)
    private String cardLast4; // For cards
    
    @Column(length = 50)
    private String errorCode;
    
    @Column(columnDefinition = "TEXT")
    private String errorDescription;
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
