package com.nikhil.wallet_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditRequest {
    private Long walletId;

    private BigDecimal amount;
}
