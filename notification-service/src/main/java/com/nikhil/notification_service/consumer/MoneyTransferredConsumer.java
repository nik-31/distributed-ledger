package com.nikhil.notification_service.consumer;

import com.nikhil.notification_service.event.MoneyTransferredEvent;
import com.nikhil.notification_service.event.NotificationFailedEvent;
import com.nikhil.notification_service.event.NotificationSentEvent;
import com.nikhil.notification_service.kafka.NotificationStatusProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class MoneyTransferredConsumer {
    private final NotificationStatusProducer producer;

    @KafkaListener(topics = "money-transferred", groupId = "notification-group-v2")
    public void consume(MoneyTransferredEvent event) {

        log.info("Transfer Received: {}", event);

        if (event.getAmount()
                .compareTo(new BigDecimal("1000")) > 0) {

            NotificationFailedEvent failedEvent =
                    NotificationFailedEvent.builder()
                            .referenceId(event.getReferenceId())
                            .reason("Notification service simulated failure")
                            .timestamp(LocalDateTime.now())
                            .build();

            producer.publishFailure(failedEvent);

            log.info("Published NotificationFailedEvent {}", event.getReferenceId());
            return;
        }

        NotificationSentEvent successEvent =
                NotificationSentEvent.builder()
                        .referenceId(event.getReferenceId())
                        .timestamp(LocalDateTime.now())
                        .build();

        producer.publishSuccess(successEvent);

        log.info("Published NotificationSentEvent {}", event.getReferenceId());
    }
}