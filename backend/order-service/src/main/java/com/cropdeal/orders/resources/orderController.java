package com.cropdeal.orders.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cropdeal.orders.dto.OrderRequest;
import com.cropdeal.orders.dto.OrderResponseDTO;
import com.cropdeal.orders.services.OrderService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/orders")
public class orderController {

    @Autowired
    private OrderService orderService;

    /**
     * Adds a new order to the system.
     * @param request Order details.
     * @param dealerEmail The email of the dealer, passed as a query parameter.
     * @return OrderResponseDTO of the created order.
     */
    @PostMapping("/addOrder")
    public ResponseEntity<OrderResponseDTO> addOrder(@Valid @RequestBody OrderRequest request,
                                                     @RequestParam String dealerEmail) { // Added dealerEmail
        try {
            OrderResponseDTO createdOrder = orderService.createOrder(
                    request.getDealerId(),
                    request.getFarmerId(),
                    request.getCropId(),
                    request.getQuantity(),
                    request.getOrderStatus(),
                    dealerEmail // Pass the dealer's email to the service
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Retrieves all orders in the system.
     * @return List of all orders.
     */
    @GetMapping("/getAllOrder")
    public List<OrderResponseDTO> getAllOrder() {
        return orderService.getAllOrders();
    }

    /**
     * Retrieves orders placed by a specific farmer.
     * @param farmerID The ID of the farmer.
     * @return List of orders by the farmer.
     */
    @GetMapping("/getOrderByFarmer/{farmerID}")
    public List<OrderResponseDTO> getOrderByFarmer(@PathVariable Long farmerID) {
        return orderService.getOrdersByFarmerId(farmerID);
    }

    /**
     * Retrieves orders made by a specific dealer.
     * @param dealerID The ID of the dealer.
     * @return List of orders by the dealer.
     */
    @GetMapping("/getOrderByDealer/{dealerID}")
    public List<OrderResponseDTO> getOrderByDealer(@PathVariable Long dealerID) {
        return orderService.getOrdersByDealerId(dealerID);
    }

    /**
     * Marks a specific order as "DELIVERED".
     * This is the new endpoint for delivery confirmation.
     * @param orderId The unique ID of the order.
     * @return OrderResponseDTO of the updated order.
     */
    @PutMapping("/updateOrderStatus/{orderId}")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(@PathVariable String orderId) {
        try {
            OrderResponseDTO updatedOrder = orderService.markOrderAsDelivered(orderId);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
