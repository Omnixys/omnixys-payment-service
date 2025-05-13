package com.omnixys.payment.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.Locale;
import java.util.stream.Stream;

/**
 * Unterstützte Währungen mit lokalisierter Darstellung (Deutsch/Englisch).
 */
@RequiredArgsConstructor
public enum CurrencyType {

  EUR("Euro", "Euro"),
  USD("US-Dollar", "US Dollar"),
  GBP("Britisches Pfund", "British Pound"),
  CHF("Schweizer Franken", "Swiss Franc"),
  JPY("Japanischer Yen", "Japanese Yen"),
  CNY("Chinesischer Yuan", "Chinese Yuan"),
  GHS("Ghanaischer Cedi", "Ghanaian Cedi");

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
  public static CurrencyType of(String value) {
    return Stream.of(values())
        .filter(c -> c.german.equalsIgnoreCase(value) || c.english.equalsIgnoreCase(value))
        .findFirst()
        .orElse(null);
  }
}
