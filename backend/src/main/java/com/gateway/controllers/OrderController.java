package com.gateway.controllers;

import com.gateway.models.Merchant;
import com.gateway.models.Order;
import com.gateway.repositories.MerchantRepository;
import com.gateway.repositories.OrderRepository;
import com.gateway.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class OrderController {
 
 @Autowired
 private OrderService orderService;
 
 @Autowired
 private MerchantRepository merchantRepository;
 
 @Autowired
 private OrderRepository orderRepository;
 
 @PostMapping("/orders")
 public ResponseEntity<?> createOrder(
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
 
 Integer amount = null;
 if (request.get("amount") instanceof Integer) {
 amount = (Integer) request.get("amount");
 } else if (request.get("amount") instanceof Double) {
 amount = ((Double) request.get("amount")).intValue();
 }
 
 if (amount == null || amount < 100) {
 return ResponseEntity.status(HttpStatus.BAD_REQUEST)
 .body(Map.of("error", Map.of("code", "BAD_REQUEST_ERROR", "description", "amount must be at least 100")));
 }
 
 Order order = orderService.createOrder(merchant.get().getId(), request);
 return ResponseEntity.status(HttpStatus.CREATED).body(order);
 }
 
 @GetMapping("/orders/{order_id}")
 public ResponseEntity<?> getOrder(
 @PathVariable("order_id") String orderId,
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
 
 Optional<Order> order = orderRepository.findByIdAndMerchantId(orderId, merchant.get().getId());
 if (order.isEmpty()) {
 return ResponseEntity.status(HttpStatus.NOT_FOUND)
 .body(Map.of("error", Map.of("code", "NOT_FOUND_ERROR", "description", "Order not found")));
 }
 
 return ResponseEntity.ok(order.get());
 }
}
