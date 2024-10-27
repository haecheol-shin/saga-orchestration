package com.example.order;

import com.example.common.Message;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Message> kafkaTemplate;

    @Override
    @Transactional
    public void save(Order order) {
        orderRepository.save(order);
    }

    @Override
    public void sendOrderCreatedEvent(Order order) {
        Message message = new Message();
        message.setOrderId(order.getId());
        message.setQuantity(order.getQuantity());
        message.setProductName(order.getProductName());
        kafkaTemplate.send("order-created", message);
    }

    // 주문 취소 보상 트랜잭션
    @KafkaListener(topics = "compensating-order", groupId = "order-group")
    @Transactional
    public void cancelOrder(Message message) {
        System.out.println("[compensating-order] OrderService received message = " + message);

        orderRepository.cancelOrder(message.getOrderId());
    }
}
