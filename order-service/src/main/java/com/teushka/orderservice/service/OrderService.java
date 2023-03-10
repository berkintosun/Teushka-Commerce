package com.teushka.orderservice.service;

import com.teushka.orderservice.dto.InventoryDTO;
import com.teushka.orderservice.dto.OrderItemDTO;
import com.teushka.orderservice.dto.OrderRequest;
import com.teushka.orderservice.entity.Order;
import com.teushka.orderservice.entity.OrderItem;
import com.teushka.orderservice.event.OrderPlacedEvent;
import com.teushka.orderservice.repository.OrderRepository;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class OrderService {

    private OrderRepository orderRepository;
    private WebClient.Builder webClientBuilder;
    private Tracer tracer;
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public OrderService(OrderRepository orderRepository, WebClient.Builder webClientBuilder, Tracer tracer, KafkaTemplate kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.webClientBuilder = webClientBuilder;
        this.tracer = tracer;
        this.kafkaTemplate = kafkaTemplate;
    }

    public String placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString());
        order.setOrderItems(
                orderRequest.getOrderItems()
                .stream()
                .map(this::mapToOrderItem)
                .toList());
        List<String> skuCodes = order.getOrderItems().stream().map(OrderItem::getSkuCode).toList();

        Span inventoryLookup = tracer.nextSpan().name("inventoryLookup");
        try(Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryLookup.start())){
            InventoryDTO[] inventoryDTOS = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory",
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
            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNo()));
            return "Order placed successfully";
        } finally {
            inventoryLookup.end();
        }
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
