package com.ecommerce.farm_basket.controller;

import com.ecommerce.farm_basket.entity.Order;
import com.ecommerce.farm_basket.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/add")
    public Order add(@RequestBody Order order, @RequestHeader("Authorization") String authHeader){
        return orderService.addOrder(order, authHeader);
    }

    @GetMapping("/get")
    public List<Order> getMy(@RequestHeader("Authorization") String authHeader){
        return orderService.getMyOrder(authHeader);
    }
}
