package com.nikhil.wallet_service.service;

import com.nikhil.wallet_service.entity.*;
import com.nikhil.wallet_service.event.MoneyTransferredEvent;
import com.nikhil.wallet_service.exception.InsufficientBalanceException;
import com.nikhil.wallet_service.exception.WalletNotFoundException;
import com.nikhil.wallet_service.kafka.MoneyTransferredProducer;
import com.nikhil.wallet_service.repository.*;
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
    private final WalletTransactionRepository walletTransactionRepository;
    private final MoneyTransferredProducer moneyTransferredProducer;

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

    private void createTransferLedgerEntries(Long fromWalletId, Long toWalletId,
            BigDecimal amount, String referenceId) {

        createLedgerEntry(fromWalletId, amount, TransactionType.DEBIT,
                referenceId, "Transfer to wallet " + toWalletId);

        createLedgerEntry(toWalletId, amount, TransactionType.CREDIT,
                referenceId, "Transfer from wallet " + fromWalletId);
    }

    private Wallet getWallet(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() ->
                        new WalletNotFoundException("Wallet not found"));
    }

    private void validateTransfer(Wallet sourceWallet, Long fromWalletId,
            Long toWalletId, BigDecimal amount) {

        if (fromWalletId.equals(toWalletId)) {
            throw new IllegalArgumentException("Source and destination wallets cannot be same");
        }

        if (sourceWallet.getBalance()
                .compareTo(amount) < 0) {

            throw new InsufficientBalanceException("Insufficient balance");
        }
    }

    private WalletTransaction createTransaction(String referenceId, Long fromWalletId,
            Long toWalletId, BigDecimal amount) {

        WalletTransaction transaction = WalletTransaction.builder()
                        .referenceId(referenceId)
                        .fromWalletId(fromWalletId)
                        .toWalletId(toWalletId)
                        .amount(amount)
                        .status(TransactionStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .build();

        return walletTransactionRepository.save(transaction);
    }

    private void updateBalances(Wallet sourceWallet, Wallet destinationWallet, BigDecimal amount) {

        sourceWallet.setBalance(sourceWallet.getBalance()
                        .subtract(amount));

        destinationWallet.setBalance(destinationWallet.getBalance()
                        .add(amount));

        walletRepository.save(sourceWallet);
        walletRepository.save(destinationWallet);
    }

    private void markTransactionSuccess(WalletTransaction transaction) {

        transaction.setStatus(TransactionStatus.SUCCESS);

        walletTransactionRepository.save(transaction);
    }

    private void saveProcessedRequest(String idempotencyKey, String referenceId) {

        ProcessedRequest request = ProcessedRequest.builder()
                        .idempotencyKey(idempotencyKey)
                        .referenceId(referenceId)
                        .createdAt(LocalDateTime.now())
                        .build();

        processedRequestRepository.save(request);
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

        Wallet sourceWallet = getWallet(fromWalletId);
        Wallet destinationWallet = getWallet(toWalletId);

        validateTransfer(sourceWallet, fromWalletId, toWalletId, amount);

        String referenceId = UUID.randomUUID().toString();

        WalletTransaction transaction = createTransaction(referenceId, fromWalletId, toWalletId, amount);

        updateBalances(sourceWallet, destinationWallet, amount);

        createTransferLedgerEntries(fromWalletId, toWalletId, amount, referenceId);

        saveProcessedRequest(idempotencyKey, referenceId);

        markTransactionSuccess(transaction);

        MoneyTransferredEvent event = MoneyTransferredEvent.builder()
                        .referenceId(referenceId)
                        .fromWalletId(fromWalletId)
                        .toWalletId(toWalletId)
                        .amount(amount)
                        .timestamp(LocalDateTime.now())
                        .build();

        moneyTransferredProducer.publish(event);

        return referenceId;
    }
}
