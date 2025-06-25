package com.ecommerce.farm_basket.service;

import com.ecommerce.farm_basket.entity.Order;
import com.ecommerce.farm_basket.entity.OrderItem;
import com.ecommerce.farm_basket.entity.User;
import com.ecommerce.farm_basket.entity.Vegetable;
import com.ecommerce.farm_basket.repository.OrderRepository;
import com.ecommerce.farm_basket.repository.UserRepository;
import com.ecommerce.farm_basket.repository.VegetableRepository;
import com.ecommerce.farm_basket.security.JwtUtil;
import io.jsonwebtoken.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    OrderRepository orderRepository;

    @Autowired
    VegetableRepository vegetableRepository;

    @Autowired
    JwtUtil jwtUtil;

    public Order addOrder(Order order, String authHeader) {
        try {
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
            String userId = jwtUtil.extractUserId(token);

            order.setCustomerId(userId);

            User customer = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            Double totalAmount = 0.0;
            Set<String> notifiedFarmers = new HashSet<>();

            for (OrderItem item : order.getItems()) {
                Vegetable vegetable = vegetableRepository.findById(item.getVegetableId())
                        .orElseThrow(() -> new RuntimeException("Vegetable is not found: " + item.getVegetableId()));

                if (vegetable.getAvailableQuantityKg() >= item.getQuantityKg()) {
                    item.setPricePerHalfKg(vegetable.getPricePerHalfKg());
                    Double itemTotal = item.getPricePerHalfKg() * (item.getQuantityKg() * 2);
                    item.setTotalPrice(itemTotal);
                    item.setName(vegetable.getName());
                    totalAmount += itemTotal;

                    vegetable.setAvailableQuantityKg(vegetable.getAvailableQuantityKg() - item.getQuantityKg());

                    vegetableRepository.save(vegetable);
                } else {
                    throw new RuntimeException("Not enough stock for " + vegetable.getName());
                }
            }

            order.setTotalAmount(totalAmount);
            Order savedOrder = orderRepository.save(order);

            // Notify farmers
            for (OrderItem item : order.getItems()) {
                Vegetable vegetable = vegetableRepository.findById(item.getVegetableId())
                        .orElseThrow(() -> new RuntimeException("Vegetable is not found"));

                String farmerId = vegetable.getFarmerId();
                if (!notifiedFarmers.contains(farmerId)) {
                    User farmer = userRepository.findById(farmerId)
                            .orElseThrow(() -> new RuntimeException("Farmer not found"));

                    StringBuilder farmerEmailBody = new StringBuilder();
                    farmerEmailBody.append("Hi ").append(farmer.getName()).append(",\n\n")
                            .append("You have received a new order from ").append(customer.getName()).append(" (")
                            .append(customer.getEmail()).append(").\n\n")
                            .append("Order Details:\n");

                    for (OrderItem oi : order.getItems()) {
                        Vegetable veg = vegetableRepository.findById(oi.getVegetableId()).orElseThrow();

                        if (veg.getFarmerId().equals(farmerId)) {
                            farmerEmailBody.append("• Vegetable: ").append(veg.getName())
                                    .append("\n  Quantity: ").append(oi.getQuantityKg()).append(" Kg")
                                    .append("\n  Price per 0.5 Kg: ₹").append(oi.getPricePerHalfKg())
                                    .append("\n  Total Price: ₹").append(oi.getTotalPrice())
                                    .append("\n\n");
                        }
                    }

                    farmerEmailBody.append("Please process the order soon.\n\nRegards,\nFarmBasket Team");

                    emailService.sendEmail(
                            farmer.getEmail(),
                            "New Order Received - FarmBasket",
                            farmerEmailBody.toString()
                    );

                    notifiedFarmers.add(farmerId);
                }
            }

            // Send confirmation to customer
            StringBuilder customerEmailBody = new StringBuilder();
            customerEmailBody.append("Hi ").append(customer.getName()).append(",\n\n")
                    .append("Your order has been placed successfully.\n\nOrder Summary:\n");

            for (OrderItem item : order.getItems()) {
                customerEmailBody.append("• ").append(item.getName())
                        .append("\n  Quantity: ").append(item.getQuantityKg()).append(" Kg")
                        .append("\n  Total: ₹").append(item.getTotalPrice())
                        .append("\n\n");
            }

            customerEmailBody.append("Total Amount: ₹").append(totalAmount)
                    .append("\n\nThank you for shopping with FarmBasket!");

            emailService.sendEmail(
                    customer.getEmail(),
                    "Order Confirmation - FarmBasket",
                    customerEmailBody.toString()
            );

            return savedOrder;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public List<Order> getMyOrder(String authHeader) {
        try{
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

            String userId = jwtUtil.extractUserId(token);

            List<Order> orders = orderRepository.findByCustomerId(userId);

            return orders;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
