package com.sipios.refactoring.dao.model;

public class ProductInfo {

    private final String type;
    private final double price;
    private final double discountedRateOnSales;

    public ProductInfo(String type, double price, double discountedRateOnSales) {
        this.type = type;
        this.price = price;
        this.discountedRateOnSales = discountedRateOnSales;
    }

    public String getType() {
        return type;
    }

    public double getDiscountedRateOnSales() {
        return discountedRateOnSales;
    }

    public double getPrice() {
        return price;
    }
}
