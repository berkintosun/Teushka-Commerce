package com.teushka.productservice.controller;

import com.teushka.productservice.dto.ProductCreationReq;
import com.teushka.productservice.dto.ProductDTO;
import com.teushka.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductCreationReq dto){
        productService.createProduct(dto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDTO> getAllProducts(){
        return productService.getAllProducts();
    }
}
