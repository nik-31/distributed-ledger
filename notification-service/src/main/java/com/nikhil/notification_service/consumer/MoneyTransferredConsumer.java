package com.nikhil.notification_service.consumer;

import com.nikhil.notification_service.event.MoneyTransferredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MoneyTransferredConsumer {

    @KafkaListener(topics = "money-transferred", groupId = "notification-group-v2")
    public void consume(MoneyTransferredEvent event) {

        log.info("Transfer Received: {}", event);

        log.info("Notification Sent for transaction {}", event.getReferenceId());
    }
}