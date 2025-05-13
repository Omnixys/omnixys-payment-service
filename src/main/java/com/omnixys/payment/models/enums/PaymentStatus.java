package com.omnixys.payment.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.Locale;
import java.util.stream.Stream;

/**
 * Zahlungsstatus mit internationaler Darstellung (Deutsch/Englisch).
 */
@RequiredArgsConstructor
public enum PaymentStatus {

    PENDING("Ausstehend", "Pending"),
    PROCESSING("Wird verarbeitet", "Processing"),
    COMPLETED("Abgeschlossen", "Completed"),
    FAILED("Fehlgeschlagen", "Failed"),
    CANCELLED("Storniert", "Cancelled"),
    REFUNDED("Erstattet", "Refunded");

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
    public static PaymentStatus of(String value) {
        return Stream.of(values())
            .filter(s -> s.german.equalsIgnoreCase(value) || s.english.equalsIgnoreCase(value))
            .findFirst()
            .orElse(null);
    }
}
