package com.cropdeal.mail.service;

import com.cropdeal.mail.dto.OrderConfirmationEmailDTO;
import com.cropdeal.mail.dto.DeliveryConfirmationEmailDTO; // NEW: Import the new DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class OTPService {

    private static final Logger log = LoggerFactory.getLogger(OTPService.class);
    private final Map<String, String> otpCache = new HashMap<>(); // Stores OTPs
    private final Map<String, ScheduledExecutorService> otpSchedulers = new HashMap<>(); // Stores schedulers for OTP expiry


    @Autowired
    private JavaMailSender mailSender;

    // Generates a 6-digit OTP
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // Generates and sends OTP to the given email
    public void generateAndSendOTP(String email) {
        String otp = generateOTP();
        otpCache.put(email, otp); // Store OTP in cache

        // Schedule OTP for expiry after 60 seconds
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        otpSchedulers.put(email, scheduler);
        scheduler.schedule(() -> {
            otpCache.remove(email); // Remove OTP from cache after expiry
            otpSchedulers.remove(email);
            log.info("OTP for {} expired.", email);
        }, 300, TimeUnit.SECONDS);

        sendEmail(
        	    email,
        	    "Your CropDeal OTP",
        	    "<html>" +
        	    "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>" +
        	    "<h2 style='color: #2E8B57;'>🌾 Welcome to CropDeal!</h2>" +
        	    "<p>Dear user,</p>" +
        	    "<p>Your <strong>One-Time Password (OTP)</strong> is:</p>" +
        	    "<div style='font-size: 24px; font-weight: bold; color: #333; margin: 10px 0;'>" + otp + "</div>" +
        	    "<p>This OTP is valid for <strong>5 minutes</strong>. Please do not share it with anyone.</p>" +
        	    "<hr style='border: none; border-top: 1px solid #ccc;'/>" +
        	    "<p style='font-size: 12px; color: #888;'>If you did not request this OTP, please ignore this email.</p>" +
        	    "<p style='font-size: 12px; color: #888;'>Thank you,<br/>Team CropDeal</p>" +
        	    "</body>" +
        	    "</html>"
        	);
        log.info("OTP generated and sent to {}: {}", email, otp);
    }

    // Verifies the OTP
    public boolean verifyOTP(String email, String otp) {
        String storedOtp = otpCache.get(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpCache.remove(email); // Invalidate OTP after successful verification
            ScheduledExecutorService scheduler = otpSchedulers.remove(email);
            if (scheduler != null) {
                scheduler.shutdownNow(); // Stop the expiry scheduler
            }
            log.info("OTP for {} verified successfully.", email);
            return true;
        }
        log.warn("OTP verification failed for {}. Provided OTP: {}, Stored OTP: {}", email, otp, storedOtp);
        return false;
    }

    // Helper method to send a simple email
    private void sendEmail(String to, String subject, String text) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true for multipart message
            helper.setFrom("parallelechoes45@gmail.com"); // Set your sender email here
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // true for HTML content
            mailSender.send(message);
            log.info("Email sent to {} with subject: {}", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sends an order confirmation email to the dealer.
     * @param orderDetails DTO containing all order details for the email.
     */
    public void sendOrderConfirmationEmail(OrderConfirmationEmailDTO orderDetails) {
        String emailBody = buildOrderConfirmationHtml(orderDetails);
        String displayOrderId = orderDetails.getOrderId() != null && orderDetails.getOrderId().length() >= 8 ?
                                orderDetails.getOrderId().substring(0, 8) : orderDetails.getOrderId();
        sendEmail(orderDetails.getDealerEmail(), "Your CropDeal Order Confirmation - Order #" + displayOrderId, emailBody);
        log.info("Order confirmation email sent to dealer {} for order {}", orderDetails.getDealerEmail(), orderDetails.getOrderId());
    }

    /**
     * Builds the HTML content for the order confirmation email.
     * @param orderDetails The DTO containing order details.
     * @return HTML string for the email body.
     */
    private String buildOrderConfirmationHtml(OrderConfirmationEmailDTO orderDetails) {
        StringBuilder html = new StringBuilder();
        html.append("<html>")
            .append("<head>")
            .append("<style>")
            .append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }")
            .append(".container { width: 80%; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: #f9f9f9; }")
            .append(".header { background-color: #4CAF50; color: white; padding: 10px 20px; text-align: center; border-radius: 8px 8px 0 0; }")
            .append(".content { padding: 20px; }")
            .append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }")
            .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }")
            .append("th { background-color: #f2f2f2; }")
            .append(".total { font-size: 1.2em; font-weight: bold; text-align: right; margin-top: 20px; }")
            .append(".footer { margin-top: 30px; font-size: 0.9em; color: #777; text-align: center; }")
            .append("</style>")
            .append("</head>")
            .append("<body>")
            .append("<div class='container'>")
            .append("<div class='header'>")
            .append("<h2>CropDeal Order Confirmation</h2>")
            .append("</div>")
            .append("<div class='content'>")
            .append("<p>Dear ").append(orderDetails.getDealerName()).append(",</p>")
            .append("<p>Thank you for your order! Your order #<strong>");

        String orderIdForDisplay = orderDetails.getOrderId() != null && orderDetails.getOrderId().length() >= 8 ?
                                   orderDetails.getOrderId().substring(0, 8) : orderDetails.getOrderId();
        html.append(orderIdForDisplay).append("</strong> has been successfully placed and is now <strong>Out for delivery</strong>.</p>")
            .append("<p>Here are the details of your order:</p>")
            .append("<table>")
            .append("<thead><tr><th>Item</th><th>Quantity (kg)</th><th>Price/kg (₹)</th><th>Farmer</th><th>Total (₹)</th></tr></thead>")
            .append("<tbody>");

        for (OrderConfirmationEmailDTO.OrderItemDetail item : orderDetails.getItems()) {
            html.append("<tr>")
                .append("<td>").append(item.getCropName()).append("</td>")
                .append("<td>").append(item.getQuantity()).append("</td>")
                .append("<td>").append(String.format("%.2f", item.getPricePerKg())).append("</td>")
                .append("<td>").append(item.getFarmerName()).append("</td>")
                .append("<td>").append(String.format("%.2f", item.getItemTotalPrice())).append("</td>")
                .append("</tr>");
        }

        html.append("</tbody></table>")
            .append("<p class='total'>Grand Total: ₹").append(String.format("%.2f", orderDetails.getTotalAmount())).append("</p>")
            .append("<p>We will notify you once your order is delivered.</p>")
            .append("<p>Best regards,<br/>The CropDeal Team</p>")
            .append("</div>")
            .append("<div class='footer'>")
            .append("<p>&copy; 2025 CropDeal. All rights reserved.</p>")
            .append("</div>")
            .append("</div>")
            .append("</body>")
            .append("</html>");

        return html.toString();
    }

    // NEW: Method to send delivery confirmation email
    public void sendDeliveryConfirmationEmail(DeliveryConfirmationEmailDTO deliveryDetails) {
        String emailBody = buildDeliveryConfirmationHtml(deliveryDetails);
        String displayOrderId = deliveryDetails.getOrderId() != null && deliveryDetails.getOrderId().length() >= 8 ?
                                deliveryDetails.getOrderId().substring(0, 8) : deliveryDetails.getOrderId();
        sendEmail(deliveryDetails.getDealerEmail(), "Your CropDeal Order #" + displayOrderId + " Has Been Delivered!", emailBody);
        log.info("Delivery confirmation email sent to dealer {} for order {}", deliveryDetails.getDealerEmail(), deliveryDetails.getOrderId());
    }

    /**
     * Builds the HTML content for the delivery confirmation email.
     * @param deliveryDetails The DTO containing delivery details.
     * @return HTML string for the email body.
     */
    private String buildDeliveryConfirmationHtml(DeliveryConfirmationEmailDTO deliveryDetails) {
        StringBuilder html = new StringBuilder();
        html.append("<html>")
            .append("<head>")
            .append("<style>")
            .append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }")
            .append(".container { width: 80%; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: #f9f9f9; }")
            .append(".header { background-color: #4CAF50; color: white; padding: 10px 20px; text-align: center; border-radius: 8px 8px 0 0; }")
            .append(".content { padding: 20px; }")
            .append(".highlight { color: #4CAF50; font-weight: bold; }")
            .append(".footer { margin-top: 30px; font-size: 0.9em; color: #777; text-align: center; }")
            .append("</style>")
            .append("</head>")
            .append("<body>")
            .append("<div class='container'>")
            .append("<div class='header'>")
            .append("<h2>Your CropDeal Order Has Been Delivered!</h2>")
            .append("</div>")
            .append("<div class='content'>")
            .append("<p>Dear ").append(deliveryDetails.getDealerName()).append(",</p>")
            .append("<p>Great news! Your order #<strong>");

        String orderIdForDisplay = deliveryDetails.getOrderId() != null && deliveryDetails.getOrderId().length() >= 8 ?
                                   deliveryDetails.getOrderId().substring(0, 8) : deliveryDetails.getOrderId();
        html.append(orderIdForDisplay).append("</strong> for <span class='highlight'>")
            .append(deliveryDetails.getCropName()).append(" (").append(deliveryDetails.getQuantity()).append(" kg)</span> has been successfully <span class='highlight'>DELIVERED</span>.</p>")
            .append("<p>Total Amount Paid: ₹").append(String.format("%.2f", deliveryDetails.getTotalPrice())).append("</p>")
            .append("<p>Thank you for shopping with CropDeal!</p>")
            .append("<p>Best regards,<br/>The CropDeal Team</p>")
            .append("</div>")
            .append("<div class='footer'>")
            .append("<p>&copy; 2025 CropDeal. All rights reserved.</p>")
            .append("</div>")
            .append("</div>")
            .append("</body>")
            .append("</html>");

        return html.toString();
    }
}
