package com.nikhil.notification_service.event;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MoneyTransferredEvent {

    private String referenceId;

    private Long fromWalletId;

    private Long toWalletId;

    private BigDecimal amount;

    private LocalDateTime timestamp;
}