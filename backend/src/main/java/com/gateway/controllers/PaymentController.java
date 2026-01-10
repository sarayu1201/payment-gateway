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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ValidationService validationService;

    @PostMapping("/payments")
    public ResponseEntity<Map> createPayment(
            @RequestHeader(value="X-Api-Key", required=false) String apiKey,
            @RequestHeader(value="X-Api-Secret", required=false) String apiSecret,
            @RequestBody Map<String, Object> request) {

        if (apiKey == null || apiSecret == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", Map.of("code", "AUTHENTICATION_ERROR", "description", "Missing API credentials")));
        }

        Optional<Merchant> merchant = merchantRepository.findByApiKey(apiKey);
        if (merchant.isEmpty() || !merchant.get().getApiSecret().equals(apiSecret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", Map.of("code", "AUTHENTICATION_ERROR", "description", "Invalid API credentials")));
        }

        String orderId = (String) request.get("order_id");
        if (orderId == null || orderId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", Map.of("code", "BAD_REQUEST_ERROR", "description", "order_id is required")));
        }

        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", Map.of("code", "NOT_FOUND_ERROR", "description", "Order not found")));
        }

        if (!order.get().getMerchantId().equals(merchant.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", Map.of("code", "BAD_REQUEST_ERROR", "description", "Order does not belong to this merchant")));
        }

        String method = (String) request.get("method");
        if (method == null || (!method.equals("upi") && !method.equals("card"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", Map.of("code", "BAD_REQUEST_ERROR", "description", "Invalid payment method")));
        }

        Payment payment = paymentService.createPayment(order.get(), method, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<Map> getPayment(
            @PathVariable("paymentId") String paymentId,
            @RequestHeader(value="X-Api-Key", required=false) String apiKey,
            @RequestHeader(value="X-Api-Secret", required=false) String apiSecret) {

        if (apiKey == null || apiSecret == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", Map.of("code", "AUTHENTICATION_ERROR", "description", "Missing API credentials")));
        }

        Optional<Merchant> merchant = merchantRepository.findByApiKey(apiKey);
        if (merchant.isEmpty() || !merchant.get().getApiSecret().equals(apiSecret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", Map.of("code", "AUTHENTICATION_ERROR", "description", "Invalid API credentials")));
        }

        Optional<Payment> payment = paymentRepository.findById(paymentId);
        if (payment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", Map.of("code", "NOT_FOUND_ERROR", "description", "Payment not found")));
        }

        if (!payment.get().getMerchantId().equals(merchant.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", Map.of("code", "BAD_REQUEST_ERROR", "description", "Payment does not belong to this merchant")));
        }

        return ResponseEntity.ok(payment.get());
    }
}
