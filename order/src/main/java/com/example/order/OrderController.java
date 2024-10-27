package com.example.order;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/save")
    public ResponseEntity<Order> save(@RequestBody Order order) {
        orderService.save(order);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/start")
    public ResponseEntity<Order> start(@RequestBody Order order) {
        orderService.sendOrderCreatedEvent(order);
        return ResponseEntity.ok(order);
    }
}
