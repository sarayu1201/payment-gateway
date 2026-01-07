package com.gateway.services;

import com.gateway.models.Merchant;
import com.gateway.models.Order;
import com.gateway.models.Payment;
import com.gateway.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ValidationService validationService;

    public Payment createPayment(Merchant merchant, Order order, Map<String, Object> request) {
        Payment payment = new Payment();
        payment.setId(generatePaymentId());
        payment.setOrderId(order.getId());
        payment.setMerchantId(merchant.getId());
        payment.setAmount(order.getAmount());
        payment.setCurrency(order.getCurrency());
        payment.setMethod((String) request.get("method"));
        payment.setStatus("processing");

        String method = (String) request.get("method");
        if ("upi".equals(method)) {
            payment.setVpa((String) request.get("vpa"));
        } else if ("card".equals(method)) {
            Map<String, Object> cardData = (Map<String, Object>) request.get("card");
            String cardNumber = (String) cardData.get("number");
            payment.setCardNetwork(validationService.detectCardNetwork(cardNumber));
            payment.setCardLast4(cardNumber.substring(cardNumber.length() - 4));
        }

        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        // Simulate payment processing asynchronously
        processPaymentAsync(savedPayment, method);

        return savedPayment;
    }

    private void processPaymentAsync(Payment payment, String method) {
        new Thread(() -> {
            try {
                // Get test mode settings
                String testMode = System.getenv("TEST_MODE");
                long delay;
                boolean success;

                if ("true".equals(testMode)) {
                    delay = Long.parseLong(System.getenv().getOrDefault("TEST_PROCESSING_DELAY", "1000"));
                    success = "true".equals(System.getenv().getOrDefault("TEST_PAYMENT_SUCCESS", "true"));
                } else {
                    delay = (long) (Math.random() * 5000 + 5000);
                    double successRate = "upi".equals(method) ? 0.9 : 0.95;
                    success = Math.random() < successRate;
                }

                Thread.sleep(delay);

                payment.setStatus(success ? "success" : "failed");
                if (!success) {
                    payment.setErrorCode("PAYMENT_FAILED");
                    payment.setErrorDescription("Payment processing failed");
                }
                payment.setUpdatedAt(LocalDateTime.now());

                paymentRepository.save(payment);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private String generatePaymentId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder id = new StringBuilder("pay_");
        for (int i = 0; i < 16; i++) {
            id.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return id.toString();
    }
}
