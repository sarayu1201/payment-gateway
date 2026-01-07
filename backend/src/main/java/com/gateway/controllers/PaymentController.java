package com.gateway.controllers;

import com.gateway.models.Merchant;
import com.gateway.models.Order;
import com.gateway.models.Payment;
import com.gateway.repositories.MerchantRepository;
import com.gateway.repositories.OrderRepository;
import com.gateway.repositories.PaymentRepository;
import com.gateway.services.PaymentService;
import com.gateway.services.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private ValidationService validationService;
    
    @Autowired
    private MerchantRepository merchantRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @PostMapping("/payments")
    public ResponseEntity<?> createPayment(
            @RequestHeader("X-Api-Key") String apiKey,
            @RequestHeader("X-Api-Secret") String apiSecret,
            @RequestBody Map<String, Object> request) {
        
        Optional<Merchant> merchant = merchantRepository.findByApiKey(apiKey);
        if (merchant.isEmpty() || !merchant.get().getApiSecret().equals(apiSecret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", Map.of("code", "AUTHENTICATION_ERROR", "description", "Invalid API credentials")));
        }
        
        String orderId = (String) request.get("order_id");
        Optional<Order> order = orderRepository.findByIdAndMerchantId(orderId, merchant.get().getId());
        if (order.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", Map.of("code", "NOT_FOUND_ERROR", "description", "Order not found")));
        }
        
        String method = (String) request.get("method");
        if ("upi".equals(method)) {
            String vpa = (String) request.get("vpa");
            if (vpa == null || !validationService.validateVPA(vpa)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", Map.of("code", "INVALID_VPA", "description", "Invalid VPA format")));
            }
        } else if ("card".equals(method)) {
            Map<String, Object> cardData = (Map<String, Object>) request.get("card");
            String cardNumber = (String) cardData.get("number");
            String expMonth = (String) cardData.get("expiry_month");
            String expYear = (String) cardData.get("expiry_year");
            
            if (!validationService.validateCardNumber(cardNumber)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", Map.of("code", "INVALID_CARD", "description", "Invalid card number")));
            }
            
            if (!validationService.validateCardExpiry(expMonth, expYear)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", Map.of("code", "EXPIRED_CARD", "description", "Card expired")));
            }
        }
        
        Payment payment = paymentService.createPayment(merchant.get(), order.get(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }
    
    @GetMapping("/payments/{payment_id}")
    public ResponseEntity<?> getPayment(
            @PathVariable("payment_id") String paymentId,
            @RequestHeader("X-Api-Key") String apiKey,
            @RequestHeader("X-Api-Secret") String apiSecret) {
        
        Optional<Merchant> merchant = merchantRepository.findByApiKey(apiKey);
        if (merchant.isEmpty() || !merchant.get().getApiSecret().equals(apiSecret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", Map.of("code", "AUTHENTICATION_ERROR", "description", "Invalid API credentials")));
        }
        
        Optional<Payment> payment = paymentRepository.findByIdAndMerchantId(paymentId, merchant.get().getId());
        if (payment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", Map.of("code", "NOT_FOUND_ERROR", "description", "Payment not found")));
        }
        
        return ResponseEntity.ok(payment.get());
    }
    
    @GetMapping("/test/merchant")
    public ResponseEntity<?> getTestMerchant() {
        Optional<Merchant> merchant = merchantRepository.findByEmail("test@example.com");
        if (merchant.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Test merchant not found"));
        }
        
        return ResponseEntity.ok(Map.of(
                "id", merchant.get().getId(),
                "email", merchant.get().getEmail(),
                "api_key", merchant.get().getApiKey(),
                "seeded", true
        ));
    }
}
