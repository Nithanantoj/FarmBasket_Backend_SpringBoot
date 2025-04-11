package com.ecommerce.farm_basket.repository;

import com.ecommerce.farm_basket.entity.Vegetable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VegetableRepository extends MongoRepository<Vegetable, String> {
    List<Vegetable> findByFarmerIdIn(List<String> farmerIds);
}
