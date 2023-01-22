package com.teushka.inventoryservice.controller;

import com.teushka.inventoryservice.dto.InventoryDTO;
import com.teushka.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryDTO> isInStock(@RequestParam List<String> skuCode){
        return inventoryService.isInStock(skuCode);
    }
}
