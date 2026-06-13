package com.nikhil.wallet_service.kafka;

import com.nikhil.wallet_service.event.MoneyTransferredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MoneyTransferredProducer {

    private final KafkaTemplate<String, MoneyTransferredEvent> kafkaTemplate;

    public void publish(MoneyTransferredEvent event) {
        kafkaTemplate.send(
                "money-transferred",
                event.getReferenceId(),
                event);
    }
}