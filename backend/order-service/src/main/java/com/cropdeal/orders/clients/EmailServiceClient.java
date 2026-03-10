package com.cropdeal.orders.clients;

import com.cropdeal.mail.dto.OrderConfirmationEmailDTO;
import com.cropdeal.mail.dto.DeliveryConfirmationEmailDTO; // NEW: Import the new DTO

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

// The 'name' attribute should match the application name of your email-service in Eureka
@FeignClient(name = "email-service")
public interface EmailServiceClient {

    // This method signature must match the *controller endpoint* in email-service
    // The path should be relative to the @RequestMapping("/api/otp") in OTPController
    @PostMapping("/api/otp/send-order-confirmation")
    ResponseEntity<String> sendOrderConfirmation(@RequestBody OrderConfirmationEmailDTO orderDetails);

    // NEW: Method to send delivery confirmation email
    @PostMapping("/api/otp/send-delivery-confirmation")
    ResponseEntity<String> sendDeliveryConfirmation(@RequestBody DeliveryConfirmationEmailDTO deliveryDetails);
}
