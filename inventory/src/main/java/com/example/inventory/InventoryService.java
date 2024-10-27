package com.example.inventory;

import com.example.common.Message;

public interface InventoryService {

    void save(Inventory inventory);
    void listenOrder(Message message);
    void listenCompensating(Message message);
}
