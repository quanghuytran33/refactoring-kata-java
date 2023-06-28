package com.sipios.refactoring.dao.model;

public class CustomerInfo {

    private final String type;
    private final double discountedRate;

    public CustomerInfo(String type, double discountedRate) {
        this.type = type;
        this.discountedRate = discountedRate;
    }

    public String getType() {
        return type;
    }

    public double getDiscountedRate() {
        return discountedRate;
    }

}
