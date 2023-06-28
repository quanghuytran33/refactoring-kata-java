package com.sipios.refactoring.dao.repository;

import com.sipios.refactoring.dao.model.ProductInfo;

import java.util.Map;
import java.util.Optional;

import static com.sipios.refactoring.constant.TypesConstant.ItemType.*;

public class ProductInfoRepository {

    private static final Map<String, ProductInfo> PRODUCT_IN_MEMORY = Map.of(TSHIRT, new ProductInfo(TSHIRT, 30,1),
        DRESS, new ProductInfo(DRESS, 50,0.8),
        JACKET, new ProductInfo(JACKET, 100,0.9));

    public Optional<ProductInfo> getProductByType(String type) {
        return Optional.ofNullable(PRODUCT_IN_MEMORY.get(type));
    }
}
