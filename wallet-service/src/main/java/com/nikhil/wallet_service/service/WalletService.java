package com.nikhil.wallet_service.service;

import com.nikhil.wallet_service.entity.Wallet;

import java.math.BigDecimal;

public interface WalletService {
    Wallet createWallet(Long userId);

    Wallet credit(Long walletId, BigDecimal amount);

    Wallet debit(Long walletId, BigDecimal amount);

    String transfer(Long fromWalletId, Long toWalletId, BigDecimal amount, String idempotencyKey);
}
