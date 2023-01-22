package com.teushka.inventoryservice.service;

import com.teushka.inventoryservice.dto.InventoryDTO;
import com.teushka.inventoryservice.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    public List<InventoryDTO> isInStock(List<String> skuCodes){
        return inventoryRepository.findBySkuCodeIn(skuCodes).stream()
                .map(inventory -> InventoryDTO.builder()
                        .skuCode(inventory.getSkuCode())
                        .isInStock(inventory.getQuantity() > 0)
                        .build())
                .toList();
    }
}
