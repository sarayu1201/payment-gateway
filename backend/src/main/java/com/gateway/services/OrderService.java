package com.gateway.services;

import com.gateway.models.Order;
import com.gateway.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    public Order createOrder(UUID merchantId, Map<String, Object> request) {
        Order order = new Order();
        order.setId(generateOrderId());
        order.setMerchantId(merchantId);
        order.setAmount((Integer) request.get("amount"));
        order.setCurrency((String) request.getOrDefault("currency", "INR"));
        order.setReceipt((String) request.get("receipt"));
        order.setNotes((String) request.get("notes"));
        order.setStatus("created");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
    
    private String generateOrderId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder id = new StringBuilder("order_");
        for (int i = 0; i < 16; i++) {
            id.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return id.toString();
    }
}
