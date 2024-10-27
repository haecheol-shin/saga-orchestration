package com.example.common;

import lombok.Data;

@Data
public class Message {

    private Long orderId;
    private String productName;
    private Integer quantity;
    private String message;
}
