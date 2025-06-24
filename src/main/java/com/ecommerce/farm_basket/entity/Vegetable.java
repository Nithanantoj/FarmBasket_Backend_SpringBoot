package com.ecommerce.farm_basket.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vegetables")
public class Vegetable {

    @Id
    private String id;
    private String name;
    private Double pricePerHalfKg;
    private Double availableQuantityKg;
    private String imageUrl;
    private String farmerId; // Reference to User (Farmer)

    // Getters and Setters

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getPricePerHalfKg() {
        return pricePerHalfKg;
    }

    public Double getAvailableQuantityKg() {
        return availableQuantityKg;
    }

    public String getFarmerId() {
        return farmerId;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }

    public void setAvailableQuantityKg(Double availableQuantityKg) {
        this.availableQuantityKg = availableQuantityKg;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPricePerHalfKg(Double pricePerHalfKg) {
        this.pricePerHalfKg = pricePerHalfKg;
    }
}

