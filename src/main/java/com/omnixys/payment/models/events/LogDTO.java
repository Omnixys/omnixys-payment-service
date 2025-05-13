package com.omnixys.payment.models.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Repräsentiert ein zentrales Log-Event, das über Kafka versendet wird.
 *
 * @param id          Eindeutige Log-ID
 * @param timestamp   Zeitstempel der Erstellung
 * @param level       z.B. DEBUG, INFO, WARN, ERROR, AUDIT
 * @param message     Die eigentliche Lognachricht
 * @param service     Der Service-Name (z. B. "payment-service")
 * @param context     Kontext (z. B. Klasse#Methode)
 * @param traceId     Trace-Identifier für verteiltes Tracing
 * @param spanId      Optionaler Span-Identifier (falls verfügbar)
 * @param user        Optional: Benutzername oder technische ID
 * @param environment Umgebung (z. B. "dev", "prod", "staging")
 */
public record LogDTO(
    UUID id,
    Instant timestamp,
    String level,
    String message,
    String service,
    String context,
    String traceId,
    String spanId,
    String user,
    String environment
) {}
