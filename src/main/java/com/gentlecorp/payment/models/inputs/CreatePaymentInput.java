package com.gentlecorp.payment.models.inputs;

import com.gentlecorp.payment.models.enums.CurrencyType;
import com.gentlecorp.payment.models.enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Input-Typ zur Erstellung einer Zahlung via GraphQL.
 */
public record CreatePaymentInput(
    UUID accountId,
    BigDecimal amount,
    CurrencyType currency,
    PaymentMethod method,
    UUID invoiceId,
    String orderNumber
) {}
