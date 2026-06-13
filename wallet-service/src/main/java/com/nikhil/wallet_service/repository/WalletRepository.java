package com.nikhil.wallet_service.repository;

import com.nikhil.wallet_service.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
