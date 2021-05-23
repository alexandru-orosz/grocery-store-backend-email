package com.example.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    private String imageUrl;
    private BigDecimal unitPrice;
    private int quantity;
    private String productName;

}
