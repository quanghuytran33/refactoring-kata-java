package com.sipios.refactoring.controller;

import com.sipios.refactoring.api.model.Body;
import com.sipios.refactoring.dao.model.Order;
import com.sipios.refactoring.dao.model.OrderLine;
import com.sipios.refactoring.dao.repository.CustomerInfoRepository;
import com.sipios.refactoring.dao.repository.ProductInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sipios.refactoring.constant.TypesConstant.CustomerType.*;

@RestController
@RequestMapping("/shopping")
public class ShoppingController {

    private Logger logger = LoggerFactory.getLogger(ShoppingController.class);

    private final Clock clock;
    private final CustomerInfoRepository customerInfoRepository;
    private final ProductInfoRepository productInfoRepository;

    public ShoppingController(Clock clock) {
        this.clock = clock;
        this.customerInfoRepository = new CustomerInfoRepository();
        this.productInfoRepository = new ProductInfoRepository();
    }

    @PostMapping
    public String getPrice(@RequestBody Body b) {
        double computedPrice = computePrice(b);
        validatePriceByCustomerType(computedPrice, b.getType());
        return String.valueOf(computedPrice);
    }

    private void validatePriceByCustomerType(double computedPrice, String customerType) {
        try {
            if (STANDARD_CUSTOMER.equals(customerType)) {
                if (computedPrice > 200) {
                    throw new Exception("Price (" + computedPrice + ") is too high for standard customer");
                }
            } else if (PREMIUM_CUSTOMER.equals(customerType)) {
                if (computedPrice > 800) {
                    throw new Exception("Price (" + computedPrice + ") is too high for premium customer");
                }
            } else if (PLATINUM_CUSTOMER.equals(customerType)) {
                if (computedPrice > 2000) {
                    throw new Exception("Price (" + computedPrice + ") is too high for platinum customer");
                }
            } else {
                if (computedPrice > 200) {
                    throw new Exception("Price (" + computedPrice + ") is too high for standard customer");
                }
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    //TODO move later to OrderService the code after this line
    public double computePrice(Body body) {
        if (body.getItems() == null) {
            return 0;
        }
        Order order = mapBodyToOrder(body);
        double totalPriceLines = computeTotalOrderLine(order.getOrderLines());
        return order.getCustomer().getDiscountedRate() * totalPriceLines;
    }

    private double computeTotalOrderLine(List<OrderLine> lines) {
        if (isNotSalesPeriod()) {
            return lines.stream()
                .mapToDouble(orderLine ->
                    orderLine.getProduct().getPrice() *
                        orderLine.getQuantity())
                .sum();
        } else {
            return lines.stream()
                .mapToDouble(orderLine ->
                    orderLine.getProduct().getPrice() *
                        orderLine.getProduct().getDiscountedRateOnSales() *
                        orderLine.getQuantity())
                .sum();
        }
    }

    private Order mapBodyToOrder(Body body) {
        List<OrderLine> orderLines = Stream.of(body.getItems())
            .map(item -> {
                if(productInfoRepository.getProductByType(item.getType()).isPresent()) {
                    return new OrderLine(productInfoRepository.getProductByType(item.getType()).get(), item.getNb());
                }
                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        return new Order(customerInfoRepository.getCustomerByType(body.getType()), orderLines);
    }

    public boolean isNotSalesPeriod() {
        return !isSalesPeriod();
    }

    public boolean isSalesPeriod() {
        LocalDate localDate = LocalDate.now(clock);
        return isSummerSales(localDate) || isWinterSales(localDate);
    }

    private boolean isSummerSales(LocalDate date) {
        return date.getDayOfMonth() < 15 && date.getDayOfMonth() > 5 && date.getMonth().equals(Month.JUNE);
    }

    private boolean isWinterSales(LocalDate date) {
        return date.getDayOfMonth() < 15 && date.getDayOfMonth() > 5 && date.getMonth().equals(Month.JANUARY);
    }

}

