package com.teushka.productservice;

import com.teushka.productservice.dto.ProductCreationReq;
import com.teushka.productservice.dto.ProductDTO;
import com.teushka.productservice.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TestDataUtility {

    public TestDataUtility() { }

    ProductCreationReq getProductCreationRequest(){
        return ProductCreationReq.builder()
                .name("Test name")
                .description("Test description")
                .price(BigDecimal.valueOf(50))
                .build();
    }

    ProductDTO mapProductToProductDTO(Product product){
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }

    Product createTempProduct(){
        return Product.builder()
                .name("Temporary product")
                .description("Temporary description")
                .price(BigDecimal.TEN)
                .build();
    }
}
