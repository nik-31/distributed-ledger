package com.nikhil.notification_service.event;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NotificationSentEvent {

    private String referenceId;

    private LocalDateTime timestamp;
}