package com.nikhil.wallet_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikhil.wallet_service.entity.TransactionStatus;
import com.nikhil.wallet_service.event.NotificationSentEvent;
import com.nikhil.wallet_service.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSentConsumer {

    private final WalletTransactionRepository transactionRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "notification-sent",
            groupId = "wallet-group")
    public void consume(String payload) throws Exception {

        NotificationSentEvent event =
                objectMapper.readValue(
                        payload,
                        NotificationSentEvent.class);

        transactionRepository
                .findByReferenceId(event.getReferenceId())
                .ifPresent(transaction -> {

                    transaction.setStatus(TransactionStatus.SUCCESS);

                    transactionRepository.save(transaction);

                    log.info(
                            "Transaction {} marked SUCCESS",
                            event.getReferenceId());
                });
    }
}