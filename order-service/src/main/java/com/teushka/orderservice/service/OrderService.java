package com.teushka.orderservice.service;

import com.teushka.orderservice.dto.OrderItemDTO;
import com.teushka.orderservice.dto.OrderRequest;
import com.teushka.orderservice.entity.Order;
import com.teushka.orderservice.entity.OrderItem;
import com.teushka.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {

    private OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString());
        order.setOrderItems(
                orderRequest.getOrderItems()
                .stream()
                .map(this::mapToOrderItem)
                .toList());
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
