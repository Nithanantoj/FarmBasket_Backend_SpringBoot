package com.ecommerce.farm_basket.repository;


import com.ecommerce.farm_basket.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface OrderRepository extends MongoRepository<Order, String> {
     List<Order> findByCustomerId(String userId);
}

