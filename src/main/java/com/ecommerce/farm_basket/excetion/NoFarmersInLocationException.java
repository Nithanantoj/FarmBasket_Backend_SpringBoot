package com.ecommerce.farm_basket.excetion;

public class NoFarmersInLocationException extends RuntimeException {
    public NoFarmersInLocationException(String message) {
        super(message);
    }
}
