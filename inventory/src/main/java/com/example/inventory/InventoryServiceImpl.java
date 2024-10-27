package com.example.inventory;

import com.example.common.Message;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Message> kafkaTemplate;

    @Override
    @Transactional
    public void save(Inventory inventory) {
        inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    @KafkaListener(topics = "check-inventory", groupId = "inventory-group")
    public void listenOrder(Message message) {
        System.out.println("[check-inventory] InventoryService received orderId = " + message.getOrderId());
        Optional<Inventory> product = inventoryRepository.findByProductName(message.getProductName());

        message.setMessage(String.valueOf(product.get().getStock() - message.getQuantity()));
        kafkaTemplate.send("result-inventory", message);
        inventoryRepository.updateStock(message.getProductName(), message.getQuantity());
    }

    // 재고 수량 원복 보상 트랜잭션
    @Override
    @Transactional
    @KafkaListener(topics = "compensating-inventory", groupId = "inventory-group")
    public void listenCompensating(Message message) {
        System.out.println("[compensating-inventory] InventoryService received orderId = " + message.getOrderId());
        inventoryRepository.updateStock(message.getProductName(), -message.getQuantity());

    }

}
