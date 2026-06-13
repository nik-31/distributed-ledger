package com.nikhil.wallet_service.service;

import com.nikhil.wallet_service.entity.*;
import com.nikhil.wallet_service.exception.InsufficientBalanceException;
import com.nikhil.wallet_service.exception.WalletNotFoundException;
import com.nikhil.wallet_service.repository.LedgerRepository;
import com.nikhil.wallet_service.repository.ProcessedRequestRepository;
import com.nikhil.wallet_service.repository.UserRepository;
import com.nikhil.wallet_service.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final LedgerRepository ledgerRepository;
    private final ProcessedRequestRepository processedRequestRepository;

    @Override
    public Wallet createWallet(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Wallet wallet = Wallet.builder()
                .userId(user.getId())
                .balance(BigDecimal.ZERO)
                .build();

        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public Wallet credit(Long walletId,
                         BigDecimal amount) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() ->
                        new WalletNotFoundException("Wallet not found"));

        wallet.setBalance(
                wallet.getBalance().add(amount));

        walletRepository.save(wallet);

        LedgerEntry entry = LedgerEntry.builder()
                .walletId(walletId)
                .amount(amount)
                .type(TransactionType.CREDIT)
                .createdAt(LocalDateTime.now())
                .build();

        ledgerRepository.save(entry);

        return wallet;
    }

    @Override
    @Transactional
    public Wallet debit(Long walletId,
                        BigDecimal amount) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() ->
                        new WalletNotFoundException("Wallet not found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        wallet.setBalance(
                wallet.getBalance().subtract(amount));

        walletRepository.save(wallet);

        LedgerEntry entry = LedgerEntry.builder()
                .walletId(walletId)
                .amount(amount)
                .type(TransactionType.DEBIT)
                .createdAt(LocalDateTime.now())
                .build();

        ledgerRepository.save(entry);

        return wallet;
    }

    private void createLedgerEntry(Long walletId, BigDecimal amount,
            TransactionType type, String referenceId, String description) {

        LedgerEntry entry = LedgerEntry.builder()
                        .walletId(walletId)
                        .amount(amount)
                        .type(type)
                        .referenceId(referenceId)
                        .description(description)
                        .createdAt(LocalDateTime.now())
                        .build();

        ledgerRepository.save(entry);
    }

    @Override
    @Transactional
    public String transfer(Long fromWalletId, Long toWalletId, BigDecimal amount, String idempotencyKey) {

        Optional<ProcessedRequest> existingRequest = processedRequestRepository
                                                    .findByIdempotencyKey(idempotencyKey);

        if (existingRequest.isPresent()) {
            return existingRequest
                    .get()
                    .getReferenceId();
        }

        Wallet sourceWallet = walletRepository.findById(fromWalletId)
                                .orElseThrow(() ->
                                    new WalletNotFoundException(
                                        "Source wallet not found"));

        Wallet destinationWallet = walletRepository.findById(toWalletId)
                                .orElseThrow(() ->
                                    new WalletNotFoundException(
                                        "Destination wallet not found"));

        if (sourceWallet.getBalance()
                .compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance");
        }

        if (fromWalletId.equals(toWalletId)) {
            throw new IllegalArgumentException(
                    "Source and destination wallets cannot be same");
        }
        String referenceId = UUID.randomUUID().toString();

        sourceWallet.setBalance(
                sourceWallet.getBalance().subtract(amount));
        destinationWallet.setBalance(
                destinationWallet.getBalance().add(amount));

        walletRepository.save(sourceWallet);
        walletRepository.save(destinationWallet);

        createLedgerEntry(fromWalletId, amount, TransactionType.DEBIT,referenceId, "Transfer to wallet " + toWalletId);
        createLedgerEntry(toWalletId, amount, TransactionType.CREDIT,referenceId, "Transfer from wallet " + fromWalletId);

        ProcessedRequest request = ProcessedRequest.builder()
                        .idempotencyKey(idempotencyKey)
                        .referenceId(referenceId)
                        .createdAt(LocalDateTime.now())
                        .build();

        processedRequestRepository.save(request);
        return referenceId;
    }
}
