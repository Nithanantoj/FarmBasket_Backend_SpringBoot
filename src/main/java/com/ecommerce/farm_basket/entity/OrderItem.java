package com.ecommerce.farm_basket.entity;


public class OrderItem {

    private String vegetableId;
    private String name;
    private Double quantityKg;
    private Double pricePerHalfKg;
    private Double totalPrice;

    OrderItem(String vegetableId, String name, Double quantityKg, Double pricePerHalfKg,Double totalPrice){
        this.vegetableId = vegetableId;
        this.name = name;
        this.quantityKg = quantityKg;
        this.pricePerHalfKg = pricePerHalfKg;
        this.totalPrice = totalPrice;
    }



    public String getVegetableId() {
        return vegetableId;
    }

    public Double getQuantityKg() {
        return quantityKg;
    }

    public Double getPricePerHalfKg() {
        return pricePerHalfKg;
    }

    public void setPricePerHalfKg(Double pricePerHalfKg) {
        this.pricePerHalfKg = pricePerHalfKg;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public String getName() {
        return name;
    }
}
