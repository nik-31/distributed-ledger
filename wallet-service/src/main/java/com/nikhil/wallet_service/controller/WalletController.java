package com.nikhil.wallet_service.controller;

import com.nikhil.wallet_service.dto.CreateWalletRequest;
import com.nikhil.wallet_service.dto.CreditRequest;
import com.nikhil.wallet_service.dto.DebitRequest;
import com.nikhil.wallet_service.dto.TransferRequest;
import com.nikhil.wallet_service.entity.Wallet;
import com.nikhil.wallet_service.exception.WalletNotFoundException;
import com.nikhil.wallet_service.repository.WalletRepository;
import com.nikhil.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final WalletRepository walletRepository;

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

    @GetMapping("/{id}")
    public Wallet getWallet(@PathVariable Long id) {

        return walletRepository
                .findById(id)
                .orElseThrow(() ->
                        new WalletNotFoundException("Wallet not found"));
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody TransferRequest request) {
        String referenceId = walletService.transfer(
                request.getFromWalletId(),
                request.getToWalletId(),
                request.getAmount(),
                idempotencyKey);

        return ResponseEntity.ok(referenceId);
    }
}