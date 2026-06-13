package com.nikhil.wallet_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DebitRequest {

    private Long walletId;

    private BigDecimal amount;
}
