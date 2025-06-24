package com.ecommerce.farm_basket.controller;

import com.ecommerce.farm_basket.entity.Vegetable;
import com.ecommerce.farm_basket.service.VegetableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/vegetables")
public class VegetableController {

    @Autowired
    VegetableService vegetableService;

    @PostMapping("/farmer/add")
    public Vegetable add( @RequestParam("name") String name,
                          @RequestParam("pricePerHalfKg") Double pricePerHalfKg,
                          @RequestParam("availableQuantityKg") Double availableQuantityKg,
                          @RequestParam("image") MultipartFile image,
                          @RequestHeader("Authorization") String authHeader){
        return vegetableService.addVegetable(name, pricePerHalfKg, availableQuantityKg, image, authHeader);
    }

    @GetMapping("/get")
    public List<Vegetable> get(@RequestHeader("Authorization") String authHeader){
        return vegetableService.getVegetables(authHeader);
    }

    @PutMapping("/farmer/update/{id}")
    public Vegetable update(@PathVariable String id, @RequestBody Vegetable vegetable, @RequestHeader("Authorization") String authHeader){
        return vegetableService.updateVegetables(id, vegetable, authHeader);
    }

    @DeleteMapping("/farmer/delete/{id}")
    public String delete(@PathVariable String id, @RequestHeader("Authorization") String authHeader){
        return vegetableService.deleteVegetable(id, authHeader);
    }

    @GetMapping("/farmer/get")
    public List<Vegetable> getForFarmer(@RequestHeader("Authorization") String authHeader){
        return vegetableService.getForFarmer(authHeader);
    }
}
