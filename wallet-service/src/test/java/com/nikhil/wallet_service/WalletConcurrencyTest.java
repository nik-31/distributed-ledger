package com.nikhil.wallet_service;

import com.nikhil.wallet_service.entity.Wallet;
import com.nikhil.wallet_service.repository.WalletRepository;
import com.nikhil.wallet_service.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SpringBootTest
class WalletConcurrencyTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @Test
    void concurrentDebitTest() throws Exception {

        Wallet wallet = walletRepository.findById(1L)
                .orElseThrow();

        wallet.setBalance(new BigDecimal("1000"));

        walletRepository.save(wallet);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<?> f1 = executor.submit(() ->
                walletService.debit(
                        1L,
                        new BigDecimal("700")));

        Future<?> f2 = executor.submit(() ->
                walletService.debit(
                        1L,
                        new BigDecimal("500")));

        try {
            f1.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            f2.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Wallet result = walletRepository.findById(1L)
                        .orElseThrow();

        System.out.println("Final Balance = " + result.getBalance());

        System.out.println("Version = " + result.getVersion());
    }
}