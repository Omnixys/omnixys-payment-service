package com.omnixys.payment.util;

import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * Hilfsklasse zur Lokalisierung von Enum-Werten anhand der aktuellen Benutzer-Locale.
 */
public class EnumLocalizationUtil {

    /**
     * Gibt die lokalisierte Darstellung eines Enums zurück, falls die Methode `getLocalized(Locale)` existiert.
     *
     * @param enumValue Enum-Wert
     * @return Lokalisierte String-Repräsentation oder Enum-Name als Fallback
     */
    public static String localize(Enum<?> enumValue) {
        try {
            var method = enumValue.getClass().getMethod("getLocalized", Locale.class);
            return (String) method.invoke(enumValue, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return enumValue.name(); // Fallback
        }
    }
}

// String display = EnumLocalizationUtil.localize(payment.getStatus());
