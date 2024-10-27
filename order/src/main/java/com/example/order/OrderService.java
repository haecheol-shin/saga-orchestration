package com.example.order;

public interface OrderService {

    void save(Order order);
    void sendOrderCreatedEvent(Order order);
}
