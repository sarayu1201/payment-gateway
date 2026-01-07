package com.gateway.config;

import com.gateway.models.Merchant;
import com.gateway.repositories.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private MerchantRepository merchantRepository;

    @Override
    public void run(String... args) throws Exception {
        // Seed test merchant if it doesn't exist
        if (merchantRepository.findByEmail("test@example.com").isEmpty()) {
            Merchant testMerchant = new Merchant();
            testMerchant.setId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
            testMerchant.setName("Test Merchant");
            testMerchant.setEmail("test@example.com");
            testMerchant.setApiKey("key_test_abc123");
            testMerchant.setApiSecret("secret_test_xyz789");
            testMerchant.setIsActive(true);
            testMerchant.setCreatedAt(LocalDateTime.now());
            testMerchant.setUpdatedAt(LocalDateTime.now());
            
            merchantRepository.save(testMerchant);
            System.out.println("✅ Test merchant seeded successfully!");
        }
    }
}
