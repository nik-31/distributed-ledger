package com.nikhil.wallet_service.event;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Data
public class NotificationFailedEvent {

    private String referenceId;

    private String reason;

    private LocalDateTime timestamp;
}
