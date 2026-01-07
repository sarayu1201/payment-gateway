package com.gateway.services;

import org.springframework.stereotype.Service;
import java.time.YearMonth;
import java.util.regex.Pattern;

@Service
public class ValidationService {
    private static final Pattern VPA_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+$");

    public boolean validateVPA(String vpa) {
        return VPA_PATTERN.matcher(vpa).matches();
    }

    public boolean validateCardNumber(String cardNumber) {
        String cleaned = cardNumber.replaceAll("[\\s-]", "");
        
        if (!cleaned.matches("\\d{13,19}")) {
            return false;
        }
        
        return luhnCheck(cleaned);
    }

    private boolean luhnCheck(String cardNumber) {
        int sum = 0;
        boolean isEven = false;
        
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            
            if (isEven) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            
            sum += digit;
            isEven = !isEven;
        }
        
        return (sum % 10) == 0;
    }

    public String detectCardNetwork(String cardNumber) {
        String cleaned = cardNumber.replaceAll("[\\s-]", "");
        
        if (cleaned.startsWith("4")) return "visa";
        if (cleaned.matches("^(51|52|53|54|55)\\d*$")) return "mastercard";
        if (cleaned.matches("^(34|37)\\d*$")) return "amex";
        if (cleaned.matches("^(60|65|8[1-9])\\d*$")) return "rupay";
        
        return "unknown";
    }

    public boolean validateCardExpiry(String month, String year) {
        try {
            int m = Integer.parseInt(month);
            int y = Integer.parseInt(year);
            
            if (m < 1 || m > 12) return false;
            
            if (y < 100) {
                y += 2000;
            }
            
            YearMonth expiry = YearMonth.of(y, m);
            YearMonth now = YearMonth.now();
            
            return !expiry.isBefore(now);
        } catch (Exception e) {
            return false;
        }
    }
}
