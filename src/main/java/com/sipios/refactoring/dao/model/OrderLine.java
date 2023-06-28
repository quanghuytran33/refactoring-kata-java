package com.sipios.refactoring.dao.model;

public class OrderLine {

    private final ProductInfo productInfo;
    private final int quantity;

    public OrderLine(ProductInfo productInfo, int quantity) {
        this.productInfo = productInfo;
        this.quantity = quantity;
    }

    public ProductInfo getProduct() {
        return productInfo;
    }

    public int getQuantity() {
        return quantity;
    }
}
