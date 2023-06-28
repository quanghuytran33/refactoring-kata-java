package com.sipios.refactoring.dao.repository;

import com.sipios.refactoring.dao.model.CustomerInfo;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static com.sipios.refactoring.constant.TypesConstant.CustomerType.*;

public class CustomerInfoRepository {

    private static final Map<String, CustomerInfo> CUSTOMER_IN_MEMORY = Map.of(STANDARD_CUSTOMER, new CustomerInfo(STANDARD_CUSTOMER, 1),
        PREMIUM_CUSTOMER, new CustomerInfo(PREMIUM_CUSTOMER, 0.9),
        PLATINUM_CUSTOMER, new CustomerInfo(PLATINUM_CUSTOMER, 0.5));

    public CustomerInfo getCustomerByType(String type) {
        if(CUSTOMER_IN_MEMORY.containsKey(type)) {
            return CUSTOMER_IN_MEMORY.get(type);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
