package com.cropdeal.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cropdeal.mail.service.OTPService;

import jakarta.validation.Valid;

import com.cropdeal.mail.dto.OrderConfirmationEmailDTO;
import com.cropdeal.mail.dto.DeliveryConfirmationEmailDTO; // NEW: Import the new DTO

@RestController
@RequestMapping("/api/otp") // Base mapping for OTP related endpoints
public class OTPController {

    @Autowired
    private OTPService otpService;

    @PostMapping("/send")
    public String sendOtp(@RequestParam String email) {
        otpService.generateAndSendOTP(email);
        return "OTP sent successfully to " + email;
    }

    @PostMapping("/verify")
    public String verifyOtp(@RequestParam String otp, @RequestParam String email) {
        boolean result = otpService.verifyOTP(email, otp);
        return result ? "OTP Verified!" : "Invalid or expired OTP!";
    }

    // Endpoint to send order confirmation email
    @PostMapping("/send-order-confirmation")
    public ResponseEntity<String> sendOrderConfirmation(@Valid @RequestBody OrderConfirmationEmailDTO orderDetails) {
        try {
            otpService.sendOrderConfirmationEmail(orderDetails);
            return ResponseEntity.ok("Order confirmation email sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send order confirmation email: " + e.getMessage());
        }
    }

    // NEW: Endpoint to send delivery confirmation email
    @PostMapping("/send-delivery-confirmation") // Choose a clear endpoint path
    public ResponseEntity<String> sendDeliveryConfirmation(@Valid @RequestBody DeliveryConfirmationEmailDTO deliveryDetails) {
        try {
            otpService.sendDeliveryConfirmationEmail(deliveryDetails); // Call the new service method
            return ResponseEntity.ok("Delivery confirmation email sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send delivery confirmation email: " + e.getMessage());
        }
    }
}
