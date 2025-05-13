package com.omnixys.payment.models.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionEventDTO(
    String type,
    BigDecimal amount,
    UUID sender,
    UUID receiver,
    LocalDateTime created,
    UUID paymentId
) {
}