package com.gateway.controllers;

import com.gateway.models.Merchant;
import com.gateway.repositories.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @Autowired
    private MerchantRepository merchantRepository;

    @GetMapping("/merchant")
    public ResponseEntity<?> getTestMerchant() {
        UUID testMerchantId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Optional<Merchant> merchant = merchantRepository.findById(testMerchantId);
        
        if (merchant.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("id", merchant.get().getId());
            response.put("email", merchant.get().getEmail());
            response.put("api_key", merchant.get().getApiKey());
            response.put("seeded", true);
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.notFound().build();
    }
}
