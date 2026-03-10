package com.cropdeal.orders.services;

import com.cropdeal.orders.dto.OrderResponseDTO;
import com.cropdeal.orders.models.*;
import com.cropdeal.orders.resources.*;
import com.cropdeal.orders.clients.EmailServiceClient;
import com.cropdeal.mail.dto.OrderConfirmationEmailDTO;
import com.cropdeal.mail.dto.DeliveryConfirmationEmailDTO; // NEW: Import the new DTO

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class OrderService {

    @Autowired
    private OrdersRepository orderRepository;

    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private EmailServiceClient emailServiceClient;

    /**
     * Retrieves all orders in the system.
     * @return List of OrderResponseDTO
     */
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves orders placed by a specific farmer.
     * @param farmerId The ID of the farmer.
     * @return List of OrderResponseDTO
     */
    public List<OrderResponseDTO> getOrdersByFarmerId(Long farmerId) {
        // Assuming farmerId in Orders entity is Integer based on your repository
        return orderRepository.findByFarmerId(farmerId.intValue())
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves orders made by a specific dealer.
     * @param dealerId The ID of the dealer.
     * @return List of OrderResponseDTO
     */
    public List<OrderResponseDTO> getOrdersByDealerId(Long dealerId) {
        // Assuming dealerId in Orders entity is Integer based on your repository
        return orderRepository.findByDealerId(dealerId.intValue())
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to map an Orders entity to an OrderResponseDTO.
     * This relies heavily on eager fetching configured in the Orders entity.
     * @param order The Orders entity.
     * @return OrderResponseDTO
     */
    private OrderResponseDTO mapToResponseDTO(Orders order) {
        String cropName = order.getCrop() != null ? order.getCrop().getCropName() : "N/A";
        Double cropPrice = order.getCrop() != null ? order.getCrop().getCropPrice() : 0.0;
        Integer cropQty = order.getCrop() != null ? order.getCrop().getCropQty() : 0;

        String farmerName = order.getFarmer() != null && order.getFarmer().getUser() != null ? order.getFarmer().getUser().getUsername() : "N/A";
        String farmerMobile = order.getFarmer() != null && order.getFarmer().getUser() != null ? order.getFarmer().getUser().getMobileNumber() : "N/A";
        String farmerAddress = order.getFarmer() != null && order.getFarmer().getUser() != null ? order.getFarmer().getUser().getAddress() : "N/A";

        String dealerName = order.getDealer() != null && order.getDealer().getUser() != null ? order.getDealer().getUser().getUsername() : "N/A";
        String dealerMobile = order.getDealer() != null && order.getDealer().getUser() != null ? order.getDealer().getUser().getMobileNumber() : "N/A";
        String dealerAddress = order.getDealer() != null && order.getDealer().getUser() != null ? order.getDealer().getUser().getAddress() : "N/A";

        Double totalPrice = order.getQuantity() * (order.getCrop() != null ? order.getCrop().getCropPrice() : 0.0);

        return OrderResponseDTO.builder()
                .orderID(order.getOrderID())
                .orderStatus(order.getOrderStatus())
                .cropName(cropName)
                .cropPrice(cropPrice)
                .cropQty(cropQty)
                .quantity(order.getQuantity())
                .farmerName(farmerName)
                .farmerMobile(farmerMobile)
                .farmerAddress(farmerAddress)
                .dealerName(dealerName)
                .dealerMobile(dealerMobile)
                .dealerAddress(dealerAddress)
                .totalPrice(totalPrice)
                .build();
    }

    /**
     * Creates a new order.
     * @param dealerId ID of the dealer placing the order.
     * @param farmerId ID of the farmer selling the crop.
     * @param cropId ID of the crop being ordered.
     * @param quantity Quantity of the crop ordered.
     * @param orderStatus Initial status of the order.
     * @param dealerEmail The email of the dealer to send confirmation to.
     * @return OrderResponseDTO of the created order.
     */
    @Transactional
    public OrderResponseDTO createOrder(Long dealerId, Long farmerId, Long cropId, int quantity, String orderStatus, String dealerEmail) {
        Optional<Dealer> dealerOpt = dealerRepository.findById(dealerId);
        Optional<Farmer> farmerOpt = farmerRepository.findById(farmerId);
        Optional<Crop> cropOpt = cropRepository.findById(cropId);

        if (dealerOpt.isEmpty()) {
            throw new RuntimeException("Dealer not found with ID: " + dealerId);
        }
        if (farmerOpt.isEmpty()) {
            throw new RuntimeException("Farmer not found with ID: " + farmerId);
        }
        if (cropOpt.isEmpty()) {
            throw new RuntimeException("Crop not found with ID: " + cropId);
        }

        Crop crop = cropOpt.get();
        Dealer dealer = dealerOpt.get();

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        if (quantity > crop.getCropQty()) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock (" + crop.getCropQty() + ").");
        }

        Orders order = new Orders();
        order.setOrderID(UUID.randomUUID().toString());
        order.setDealer(dealer);
        order.setFarmer(farmerOpt.get());
        order.setCrop(crop);
        order.setQuantity(quantity);
        order.setOrderStatus(orderStatus);

        Orders savedOrder = orderRepository.save(order);

        // Prepare and send order confirmation email using Feign Client
        OrderConfirmationEmailDTO.OrderItemDetail itemDetail = OrderConfirmationEmailDTO.OrderItemDetail.builder()
                .cropName(crop.getCropName())
                .quantity(quantity)
                .pricePerKg(crop.getCropPrice())
                .farmerName(farmerOpt.get().getUser().getUsername())
                .itemTotalPrice(quantity * crop.getCropPrice())
                .build();

        OrderConfirmationEmailDTO emailDTO = OrderConfirmationEmailDTO.builder()
                .dealerEmail(dealerEmail)
                .orderId(savedOrder.getOrderID())
                .totalAmount(itemDetail.getItemTotalPrice())
                .dealerName(dealer.getUser().getUsername())
                .items(List.of(itemDetail))
                .build();

        // Call the email-service via Feign Client
        try {
            ResponseEntity<String> emailResponse = emailServiceClient.sendOrderConfirmation(emailDTO);
            if (emailResponse.getStatusCode().is2xxSuccessful()) {
                System.out.println("Order confirmation email triggered successfully via Feign Client.");
            } else {
                System.err.println("Failed to trigger order confirmation email via Feign Client. Status: " + emailResponse.getStatusCode() + ", Body: " + emailResponse.getBody());
            }
        } catch (Exception e) {
            System.err.println("Error calling email-service via Feign Client: " + e.getMessage());
            e.printStackTrace();
            // Log this error, but don't prevent order creation from completing
        }

        return mapToResponseDTO(savedOrder);
    }

    /**
     * Marks an order as "DELIVERED" and sends a delivery confirmation email.
     * @param orderID The unique ID of the order to update.
     * @return OrderResponseDTO of the updated order.
     */
    @Transactional
    public OrderResponseDTO markOrderAsDelivered(String orderID) {
        Optional<Orders> orderOpt = orderRepository.findByOrderID(orderID);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found with ID: " + orderID);
        }

        Orders order = orderOpt.get();
        order.setOrderStatus("DELIVERED");

        Orders updatedOrder = orderRepository.save(order);

        // NEW: Prepare and send delivery confirmation email
        try {
            Dealer dealer = updatedOrder.getDealer();
            Crop crop = updatedOrder.getCrop();
            Farmer farmer = updatedOrder.getFarmer();

            if (dealer != null && dealer.getUser() != null && crop != null && farmer != null && farmer.getUser() != null) {
                DeliveryConfirmationEmailDTO deliveryEmailDTO = DeliveryConfirmationEmailDTO.builder()
                       // .dealerEmail(dealer.getUser().getUsername()) // Assuming username is email
                		.dealerEmail("codekaliber@gmail.com")
                        .dealerName(dealer.getUser().getUsername())
                        .orderId(updatedOrder.getOrderID())
                        .cropName(crop.getCropName())
                        .quantity(updatedOrder.getQuantity())
                        .totalPrice(updatedOrder.getQuantity() * crop.getCropPrice())
                        .farmerName(farmer.getUser().getUsername())
                        .build();

                ResponseEntity<String> emailResponse = emailServiceClient.sendDeliveryConfirmation(deliveryEmailDTO);
                if (emailResponse.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Delivery confirmation email triggered successfully via Feign Client.");
                } else {
                    System.err.println("Failed to trigger delivery confirmation email via Feign Client. Status: " + emailResponse.getStatusCode() + ", Body: " + emailResponse.getBody());
                }
            } else {
                System.err.println("Cannot send delivery confirmation email: Missing dealer, crop, or farmer details for order " + orderID);
            }
        } catch (Exception e) {
            System.err.println("Error calling email-service for delivery confirmation: " + e.getMessage());
            e.printStackTrace();
            
        }

        return mapToResponseDTO(updatedOrder);
    }
}
