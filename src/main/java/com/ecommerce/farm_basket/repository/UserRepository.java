package com.ecommerce.farm_basket.repository;

import com.ecommerce.farm_basket.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {

    User findByEmail(String email);

    List<User> findByRoleAndLocation(String farmer, String userLocation);
}
