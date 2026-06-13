package com.nikhil.notification_service.kafka;

import com.nikhil.notification_service.event.NotificationFailedEvent;
import com.nikhil.notification_service.event.NotificationSentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationStatusProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishSuccess(NotificationSentEvent event) {
        kafkaTemplate.send("notification-sent", event.getReferenceId(), event);
    }

    public void publishFailure(NotificationFailedEvent event) {
        kafkaTemplate.send("notification-failed", event.getReferenceId(), event);
    }
}