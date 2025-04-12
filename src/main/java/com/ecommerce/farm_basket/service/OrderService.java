package com.ecommerce.farm_basket.service;

import com.ecommerce.farm_basket.entity.Order;
import com.ecommerce.farm_basket.entity.OrderItem;
import com.ecommerce.farm_basket.entity.Vegetable;
import com.ecommerce.farm_basket.repository.OrderRepository;
import com.ecommerce.farm_basket.repository.VegetableRepository;
import com.ecommerce.farm_basket.security.JwtUtil;
import io.jsonwebtoken.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    VegetableRepository vegetableRepository;

    @Autowired
    JwtUtil jwtUtil;

    public Order addOrder(Order order, String authHeader) {
        try{
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

            String userId = jwtUtil.extractUserId(token);

            order.setCustomerId(userId);

            Double totalAmount = 0.0;
            for(OrderItem item : order.getItems()){
                Vegetable vegetable = vegetableRepository.findById(item.getVegetableId())
                                .orElseThrow(() -> new RuntimeException("Vegetable is not found" + item.getVegetableId()));

                if(vegetable.getAvailableQuantityKg() >= item.getQuantityKg()) {

                    item.setPricePerHalfKg(vegetable.getPricePerHalfKg());

                    Double itemTotal = item.getPricePerHalfKg() * (item.getQuantityKg() * 2);

                    item.setTotalPrice(itemTotal);
                    item.setName(vegetable.getName());

                    totalAmount += itemTotal;

                    vegetable.setAvailableQuantityKg(vegetable.getAvailableQuantityKg() - item.getQuantityKg());
                }
                else{
                    throw new RuntimeException("Not enough stock for " + vegetable.getName());
                }
                vegetableRepository.save(vegetable);
            }

            order.setTotalAmount(totalAmount);

            return orderRepository.save(order);

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
