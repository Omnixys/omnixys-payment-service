package com.omnixys.payment.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.Locale;
import java.util.stream.Stream;

/**
 * Zahlungsmethoden mit mehrsprachiger Darstellung.
 */
@RequiredArgsConstructor
public enum PaymentMethod {

    CREDIT_CARD("Kreditkarte", "Credit Card"),
    DEBIT_CARD("Debitkarte", "Debit Card"),
    PAYPAL("PayPal", "PayPal"),
    APPLE_PAY("Apple Pay", "Apple Pay"),
    GOOGLE_PAY("Google Pay", "Google Pay"),
    BANK_TRANSFER("Banküberweisung", "Bank Transfer"),
    BITCOIN("Bitcoin", "Bitcoin");

    private final String german;
    private final String english;

    public String getLocalized(Locale locale) {
        return Locale.GERMAN.getLanguage().equals(locale.getLanguage()) ? german : english;
    }

    @JsonValue
    public String getDefault() {
        return german;
    }

    @JsonCreator
    public static PaymentMethod of(String value) {
        return Stream.of(values())
            .filter(m -> m.german.equalsIgnoreCase(value) || m.english.equalsIgnoreCase(value))
            .findFirst()
            .orElse(null);
    }
}
