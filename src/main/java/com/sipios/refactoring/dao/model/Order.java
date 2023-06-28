package com.sipios.refactoring.dao.model;

import java.util.List;

public class Order {

    private final CustomerInfo customerInfo;
    private final List<OrderLine> orderLines;

    public Order(CustomerInfo customerInfo, List<OrderLine> orderLines) {
        this.customerInfo = customerInfo;
        this.orderLines = orderLines;
    }

    public CustomerInfo getCustomer() {
        return customerInfo;
    }

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }


}
