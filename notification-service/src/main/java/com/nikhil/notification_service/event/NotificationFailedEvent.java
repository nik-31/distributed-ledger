package com.nikhil.notification_service.event;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NotificationFailedEvent {

    private String referenceId;

    private String reason;

    private LocalDateTime timestamp;
}
