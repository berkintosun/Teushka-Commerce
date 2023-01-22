package com.teushka.orderservice.service;

import com.teushka.orderservice.dto.InventoryDTO;
import com.teushka.orderservice.dto.OrderItemDTO;
import com.teushka.orderservice.dto.OrderRequest;
import com.teushka.orderservice.entity.Order;
import com.teushka.orderservice.entity.OrderItem;
import com.teushka.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    private OrderRepository orderRepository;

    private WebClient webClient;

    public OrderService(OrderRepository orderRepository, WebClient webClient) {
        this.orderRepository = orderRepository;
        this.webClient = webClient;
    }

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString());
        order.setOrderItems(
                orderRequest.getOrderItems()
                .stream()
                .map(this::mapToOrderItem)
                .toList());
        List<String> skuCodes = order.getOrderItems().stream().map(OrderItem::getSkuCode).toList();
        InventoryDTO[] inventoryDTOS = webClient.get()
                .uri("http://127.0.0.1:8082/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build()
                )
                .retrieve()
                .bodyToMono(InventoryDTO[].class)
                .block();

        Boolean allItemsInStock = Arrays.stream(inventoryDTOS).allMatch(InventoryDTO::isInStock);
        if(!allItemsInStock){
            throw new IllegalArgumentException("Product is not in stock.");
        }
            orderRepository.save(order);
    }

    private OrderItem mapToOrderItem(OrderItemDTO dto) {
        OrderItem item = new OrderItem();
        item.setId(dto.getId());
        item.setSkuCode(dto.getSkuCode());
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());
        return item;
    }
}
