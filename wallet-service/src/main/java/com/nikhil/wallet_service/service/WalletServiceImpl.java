package com.nikhil.wallet_service.service;

import com.nikhil.wallet_service.entity.LedgerEntry;
import com.nikhil.wallet_service.entity.TransactionType;
import com.nikhil.wallet_service.entity.User;
import com.nikhil.wallet_service.entity.Wallet;
import com.nikhil.wallet_service.repository.LedgerRepository;
import com.nikhil.wallet_service.repository.UserRepository;
import com.nikhil.wallet_service.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final LedgerRepository ledgerRepository;


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
                        new RuntimeException("Wallet not found"));

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
                        new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException(
                    "Insufficient balance");
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
}
