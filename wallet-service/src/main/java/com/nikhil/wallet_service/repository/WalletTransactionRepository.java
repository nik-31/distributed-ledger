package com.nikhil.wallet_service.repository;

import com.nikhil.wallet_service.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
}
