package com.ecommerce.farm_basket.service;

import com.ecommerce.farm_basket.entity.User;
import com.ecommerce.farm_basket.entity.Vegetable;
import com.ecommerce.farm_basket.excetion.NoFarmersInLocationException;
import com.ecommerce.farm_basket.excetion.UserNotFoundException;
import com.ecommerce.farm_basket.repository.UserRepository;
import com.ecommerce.farm_basket.repository.VegetableRepository;
import com.ecommerce.farm_basket.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VegetableService {

    @Autowired
    VegetableRepository vegetableRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;


    public Vegetable addVegetable(Vegetable vegetable, String authHeader) {
        try{

            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

            String userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User is Not Found0"));


            vegetable.setFarmerId(userId);

            return vegetableRepository.save(vegetable);


        }
        catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to add vegetable: " + e.getMessage());
        }
    }

    public List<Vegetable> getVegetables(String authHeader) {
        try{

            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

            String userId = jwtUtil.extractUserId(token);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User is Not Found0"));

            String userLocation = user.getLocation();

//            System.out.println(userLocation);

            List<User> farmers = userRepository.findByRoleAndLocation("farmer", userLocation);

            if (farmers.isEmpty()) {
                throw new NoFarmersInLocationException("No farmers found in your location: " + userLocation);
            }

//            System.out.println(farmers);

            List<String> farmerIds = farmers.stream()
                    .map(User::getId)
                    .toList();

            return vegetableRepository.findByFarmerIdIn(farmerIds);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Vegetable updateVegetables(String id, Vegetable vegetable, String authHeader) {
        try{
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

            String userId = jwtUtil.extractUserId(token);

            Vegetable existingVeg = vegetableRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Vegetable not found"));

            if(!existingVeg.getFarmerId().equals(userId))
                throw new RuntimeException("Unauthorized: You can update only your own vegetables");

            if(vegetable.getName() != null)
            existingVeg.setName(vegetable.getName());

            if(vegetable.getAvailableQuantityKg() != null)
            existingVeg.setAvailableQuantityKg(vegetable.getAvailableQuantityKg());

            if(vegetable.getPricePerHalfKg() != null)
            existingVeg.setPricePerHalfKg(vegetable.getPricePerHalfKg());

            return vegetableRepository.save(existingVeg);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String deleteVegetable(String id, String authHeader) {
        try{
            String token = authHeader.substring(7);

            String userId = jwtUtil.extractUserId(token);

            Vegetable vegetable = vegetableRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Vegetable not found with ID: " + id));

            if(!vegetable.getFarmerId().equals(userId)){
                throw new RuntimeException("You are not authorized to delete this vegetable.");
            }

            vegetableRepository.deleteById(id);
            return "Vegetable deleted successfully.";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Vegetable> getForFarmer(String authHeader) {
        try{
            String token = authHeader.substring(7);

            String userId = jwtUtil.extractUserId(token);

            boolean exist = vegetableRepository.existsByFarmerId(userId);

            if(!exist)
                throw new RuntimeException("No vegetables found for this farmer.");

            return vegetableRepository.findByFarmerId(userId);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
