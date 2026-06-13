package com.nikhil.wallet_service.controller;

import com.nikhil.wallet_service.dto.CreateWalletRequest;
import com.nikhil.wallet_service.dto.CreditRequest;
import com.nikhil.wallet_service.dto.DebitRequest;
import com.nikhil.wallet_service.entity.Wallet;
import com.nikhil.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public Wallet createWallet(@RequestBody CreateWalletRequest request) {
        return walletService.createWallet(
                request.getUserId());
    }

    @PostMapping("/credit")
    public Wallet credit(@RequestBody CreditRequest request) {
        return walletService.credit(
                request.getWalletId(),
                request.getAmount());
    }

    @PostMapping("/debit")
    public Wallet debit(@RequestBody DebitRequest request) {

        return walletService.debit(
                request.getWalletId(),
                request.getAmount());
    }
}