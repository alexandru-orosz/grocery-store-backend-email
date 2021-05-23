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
public class Order {

    private String orderTrackingNumber;
    private int totalQuantity;
    private BigDecimal totalPrice;

}
