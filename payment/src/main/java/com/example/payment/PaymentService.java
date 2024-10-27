package com.example.payment;

import com.example.common.Message;

public interface PaymentService {

    void save(Payment payment);
    void listenInventory(Message message);
}
