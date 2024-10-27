package com.example.orchestrator;

import com.example.common.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrchestratorService {

    private final KafkaTemplate<String, Message> kafkaTemplate;

    // 주문이 생성됨을 확인
    @KafkaListener(topics = "order-created", groupId = "orchestrator-group")
    public void listenOrderCreated(Message message) {
        System.out.println("[order-created] orchestrator received message = " + message);

        // 재고 확인 요청
        kafkaTemplate.send("check-inventory", message);
    }

    // 재고가 있는지 check
    @KafkaListener(topics = "result-inventory", groupId = "orchestrator-group")
    public void listenCheckInventory(Message message) {
        System.out.println("[result-inventory] orchestrator received message = " + message);

        // 재고가 있으면 결제쪽으로 이벤트
        if (Integer.parseInt(message.getMessage()) >= 0) {
            kafkaTemplate.send("check-payment", message);
        } else { // 재고가 없으면 주문 보상 트랜색션 요청
            kafkaTemplate.send("cancel-order", message);
        }
    }

    // 결제 성공 or 실패
    @KafkaListener(topics = "result-payment", groupId = "orchestrator-group")
    public void listenCheckPayment(Message message) {
        System.out.println("[check-payment] orchestrator received message = " + message);

        // 성공이면 끝
        if (Objects.equals(message.getMessage(), "SUCCESS")) {
            System.out.println("Finished Orchestration");
        } else { // 실패면 주문 보상 트랙잭션 요청, 재고 보상 트랜잭션 요청
            kafkaTemplate.send("compensating-inventory", message);
            kafkaTemplate.send("compensating-order", message);
        }
    }

}
