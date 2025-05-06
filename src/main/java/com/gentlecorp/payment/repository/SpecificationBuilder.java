package com.gentlecorp.payment.repository;

import com.gentlecorp.payment.models.entitys.Payment;
import com.gentlecorp.payment.models.entitys.Payment_;
import com.gentlecorp.payment.models.enums.CurrencyType;
import com.gentlecorp.payment.models.enums.PaymentMethod;
import com.gentlecorp.payment.models.enums.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class SpecificationBuilder {

  /**
   * Erstellt eine JPA-Spezifikation basierend auf den Suchkriterien.
   *
   * @param queryParams Suchkriterien als Map
   * @return Optionale Spezifikation für die Filterung von Rechnungen
   */
  public Optional<Specification<Payment>> build(final Map<String, ? extends List<Object>> queryParams) {
    log.debug("build: queryParams={}", queryParams);

    if (queryParams.isEmpty()) {
      return Optional.empty();
    }

    final var specs = queryParams.entrySet().stream()
        .map(this::toSpecification)
        .filter(spec -> spec != null)
        .toList();

    if (specs.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(Specification.allOf(specs));
  }

  /**
   * Wandelt ein Suchkriterium in eine JPA-Spezifikation um.
   */
  private Specification<Payment> toSpecification(final Map.Entry<String, ? extends List<Object>> entry) {
    log.trace("toSpec: entry={}", entry);
    final var key = entry.getKey();
    final var values = entry.getValue();

    if (values == null || values.isEmpty()) return null;

    final var value = values.getFirst().toString();
    return switch (key) {
      case "accountId" -> accountId(value);
      case "username" -> username(value);
      case "status" -> status(value);
      case "method" -> method(value);
      case "currency" -> currency(value);
//      case "customerId" -> customerId(value);
      case "invoiceId" -> invoiceId(value);
      case "orderNumber" -> orderNumber(value);
      case "amountMin" -> amountMin(value);
      case "amountMax" -> amountMax(value);
      case "createdFrom" -> createdFrom(value);   // 👈 NEU
      case "createdTo" -> createdTo(value);       // 👈 NEU
      default -> {
        log.warn("Unbekanntes Filterkriterium: {}", key);
        yield null;
      }
    };
  }

  private Specification<Payment> accountId(final String value) {
    return (root, query, builder) -> builder.equal(
        root.get(Payment_.accountId),
        value);
  }

  private Specification<Payment> username(final String value) {
    return (root, query, builder) -> builder.equal(
        root.get(Payment_.username),
        value);
  }
  /**
   * Erstellt eine Spezifikation für das StatusType-Feld.
   */
  private Specification<Payment> status(final String value) {
    try {
      return (root, query, builder) -> builder.equal(
          root.get(Payment_.status),
          PaymentStatus.of(value));
    } catch (Exception e) {
      log.error("Ungültiger PaymentStatus: {}", value);
      return null;
    }
  }

  private Specification<Payment> method(final String value) {
    try {
      return (root, query, builder) -> builder.equal(
          root.get(Payment_.method),
          PaymentMethod.of(value));
    } catch (Exception e) {
      log.error("Ungültige Zahlungsmethode: {}", value);
      return null;
    }
  }

  private Specification<Payment> currency(final String value) {
    try {
      return (root, query, builder) -> builder.equal(
          root.get(Payment_.currency),
          CurrencyType.of(value)
      );
    } catch (Exception e) {
      log.error("Ungültige Währung: {}", value);
      return null;
    }
  }

//  /**
//   * Erstellt eine Spezifikation für das customerId-Feld.
//   */
//  private Specification<Payment> customerId(final String value) {
//    try {
//      UUID uuid = UUID.fromString(value);
//      return (root, query, builder) -> builder.equal(
//          root.get(Payment_.customerId),
//          uuid);
//    } catch (Exception e) {
//      log.error("Ungültige UUID für customerId: {}", value);
//      return null;
//    }
//  }

  private Specification<Payment> invoiceId(final String value) {
    try {
      UUID uuid = UUID.fromString(value);
      return (root, query, builder) -> builder.equal(
          root.get(Payment_.invoiceId),
          uuid);
    } catch (Exception e) {
      log.error("Ungültige UUID für invoiceId: {}", value);
      return null;
    }
  }

  private Specification<Payment> orderNumber(final String value) {
    return (root, query, builder) -> builder.equal(
        root.get(Payment_.orderNumber),
        value);
  }

  private Specification<Payment> amountMin(final String value) {
    try {
      BigDecimal amount = new BigDecimal(value);
      return (root, query, builder) -> builder.greaterThanOrEqualTo(
          root.get(Payment_.amount),
          amount);
    } catch (Exception e) {
      log.error("Ungültiger Betrag für amountMin: {}", value);
      return null;
    }
  }

  private Specification<Payment> amountMax(final String value) {
    try {
      BigDecimal amount = new BigDecimal(value);
      return (root, query, builder) -> builder.lessThanOrEqualTo(
          root.get(Payment_.amount),
          amount);
    } catch (Exception e) {
      log.error("Ungültiger Betrag für amountMax: {}", value);
      return null;
    }
  }

  private Specification<Payment> createdFrom(final String value) {
    try {
      final var from = LocalDateTime.parse(value);
      return (root, query, builder) -> builder.greaterThanOrEqualTo(
          root.get(Payment_.created),
          from);
    } catch (DateTimeParseException e) {
      log.error("Ungültiges Datumsformat für createdFrom: {}", value);
      return null;
    }
  }

  private Specification<Payment> createdTo(final String value) {
    try {
      final var to = LocalDateTime.parse(value);
      return (root, query, builder) -> builder.lessThanOrEqualTo(
          root.get(Payment_.created),
          to);
    } catch (DateTimeParseException e) {
      log.error("Ungültiges Datumsformat für createdTo: {}", value);
      return null;
    }
  }

}
