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
public class NotificationSentEvent {

    private String referenceId;

    private LocalDateTime timestamp;
}