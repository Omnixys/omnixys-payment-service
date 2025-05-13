package com.omnixys.payment.models.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentDTO(
    BigDecimal amount,
    BigDecimal alreadyPaid,
    UUID invoiceId
) {
}
