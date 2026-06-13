package com.nikhil.wallet_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    private Long fromWalletId;

    private Long toWalletId;

    private BigDecimal amount;

    private String idempotencyKey;
}