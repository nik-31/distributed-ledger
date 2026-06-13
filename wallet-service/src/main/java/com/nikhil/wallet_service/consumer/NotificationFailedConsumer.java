package com.nikhil.wallet_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikhil.wallet_service.entity.TransactionStatus;
import com.nikhil.wallet_service.event.NotificationFailedEvent;
import com.nikhil.wallet_service.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationFailedConsumer {

    private final WalletTransactionRepository transactionRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "notification-failed",
            groupId = "wallet-group")
    public void consume(String payload) throws Exception {

        NotificationFailedEvent event =
                objectMapper.readValue(
                        payload,
                        NotificationFailedEvent.class);

        transactionRepository
                .findByReferenceId(event.getReferenceId())
                .ifPresent(transaction -> {

                    transaction.setStatus(TransactionStatus.COMPENSATED);

                    transactionRepository.save(transaction);

                    log.info(
                            "Transaction {} marked COMPENSATED",
                            event.getReferenceId());
                });
    }
}