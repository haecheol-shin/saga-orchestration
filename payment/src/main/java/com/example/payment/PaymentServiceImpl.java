package com.example.payment;

import com.example.common.Message;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Message> kafkaTemplate;

    @Override
    @Transactional
    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    @Override
    @KafkaListener(topics = "check-payment", groupId = "payment-group")
    public void listenInventory(Message message) {
        System.out.println("[check-payment] PaymentService received message = " + message);

        Optional<Payment> payment = paymentRepository.findById(message.getOrderId());
        message.setMessage(payment.get().getPaymentStatus().toString());
        kafkaTemplate.send("result-payment", message);
    }
}
