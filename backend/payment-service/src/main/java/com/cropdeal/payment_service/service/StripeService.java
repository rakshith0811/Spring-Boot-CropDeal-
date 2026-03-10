package com.cropdeal.payment_service.service;

import com.cropdeal.payment_service.dto.ProductRequest;
import com.cropdeal.payment_service.dto.StripeResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StripeService.class);

    @Value("${stripe.secretKey}")
    private String secretKey;

    @Value("${stripe.successUrl}")
    private String successUrl;

    @Value("${stripe.cancelUrl}")
    private String cancelUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
        LOGGER.info("Stripe API key initialized");
    }

    public StripeResponse checkoutProducts(ProductRequest productRequest) {
        try {
            // Build Product Data
            SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(productRequest.getName())
                            .build();

            // Build Price Data
            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(productRequest.getCurrency() != null ? productRequest.getCurrency() : "USD")
                            .setUnitAmount(productRequest.getAmount()*100)
                            .setProductData(productData)
                            .build();

            // Build Line Item
            SessionCreateParams.LineItem lineItem =
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(productRequest.getQuantity())
                            .setPriceData(priceData)
                            .build();

            // Create Stripe Session
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .addLineItem(lineItem)
                    .build();

            Session session = Session.create(params);

            return new StripeResponse(
                    "SUCCESS",
                    "Payment session created",
                    session.getId(),
                    session.getUrl()
            );

        } catch (StripeException e) {
            LOGGER.error("Stripe exception occurred: {}", e.getMessage());
            return new StripeResponse(
                    "FAILURE",
                    "Failed to create payment session: " + e.getMessage(),
                    null,
                    null
            );
        } catch (Exception e) {
            LOGGER.error("Unexpected error during Stripe session creation", e);
            return new StripeResponse(
                    "FAILURE",
                    "Unexpected error occurred",
                    null,
                    null
            );
        }
    }
}
