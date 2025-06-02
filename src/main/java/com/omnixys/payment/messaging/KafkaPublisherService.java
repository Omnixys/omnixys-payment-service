package com.omnixys.payment.messaging;

import com.omnixys.payment.config.AppProperties;
import com.omnixys.payment.models.dto.NewPaymentIdDTO;
import com.omnixys.payment.models.entitys.Payment;
import com.omnixys.payment.models.events.LogDTO;
import com.omnixys.payment.models.events.SendMail;
import com.omnixys.payment.models.events.TransactionEventDTO;
import com.omnixys.payment.tracing.TraceContextUtil;
import io.micrometer.observation.annotation.Observed;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.omnixys.payment.messaging.KafkaTopicProperties.TOPIC_INVOICE_CREATE_PAYMENT;
import static com.omnixys.payment.messaging.KafkaTopicProperties.TOPIC_LOG_STREAM_LOG_PAYMENT;
import static com.omnixys.payment.messaging.KafkaTopicProperties.TOPIC_NOTIFICATION_CREATE_PAYMENT;
import static com.omnixys.payment.messaging.KafkaTopicProperties.TOPIC_TRANSACTION_CREATE_PAYMENT;

/**
 * Service zum Versenden von Kafka-Nachrichten im Zusammenhang mit Personenereignissen.
 * <p>
 * Unterstützte Ereignisse:
 * <ul>
 *     <li>Kundenmail nach Erstellung</li>
 *     <li>Erstellen eines Kontos</li>
 *     <li>Erstellen und Löschen eines Warenkorbs</li>
 * </ul>
 * </p>
 *
 * <p>
 * Die Topics folgen dem Schema: <code>service.entität.ereignis</code>
 * z.B. <code>shopping-cart.customer.created</code>
 * </p>
 *
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @since 01.05.2025
 * @version 2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPublisherService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Tracer tracer;
    private final KafkaUtilService kafkaUtilService;
    private final AppProperties appProperties;


    /**
     * Versendet ein Logging-Event an das zentrale Logging-System via Kafka.
     *
     * @param level   z.B. INFO, WARN, DEBUG, ERROR
     * @param message Die zu loggende Nachricht
     * @param context Kontext wie Klassen- oder Methodenname
     */
    @Observed(name = "kafka-publisher.log")
    public void log(String level, String message, String serviceName, String context) {
        SpanContext spanContext = Span.current().getSpanContext();

        final var event = new LogDTO(
            UUID.randomUUID(),
            Instant.now(),
            level,
            message,
            serviceName,
            context,
            spanContext.isValid() ? spanContext.getTraceId() : null,
            spanContext.isValid() ? spanContext.getSpanId() : null,
            TraceContextUtil.getUsernameOrNull(),
            appProperties.getEnv()
        );

        sendKafkaEvent(TOPIC_LOG_STREAM_LOG_PAYMENT, event, "log");
    }

    /**
     * Versendet ein Kafka-Event zur Bestätigungsmail beim Erstellen einer Bezahlung.
     *
     * @param payment die erstellte Bezahlung
     * @param role   die zugewiesene Rolle
     */
    @Observed(name = "kafka-publisher.send-mail")
    public void sendMail(Payment payment, String role) {
        final var mailDTO = SendMail.fromEntity(payment);
        sendKafkaEvent(TOPIC_NOTIFICATION_CREATE_PAYMENT, mailDTO, "sendMail");
    }

    public void createPayment(Payment savedPayment) {
        final var newPaymentDTO = new NewPaymentIdDTO(savedPayment.getId(), savedPayment.getInvoiceId());
        sendKafkaEvent(TOPIC_INVOICE_CREATE_PAYMENT, newPaymentDTO, "newPayment");
    }

    @Observed(name = "kafka-publisher.pay")
    public void pay(final BigDecimal paidAmount, final UUID sender, final UUID receiver, final UUID paymentId) {
        final var transactionDTO = new TransactionEventDTO("PAYMENT", paidAmount, sender, receiver, LocalDateTime.now(),paymentId);
        sendKafkaEvent(TOPIC_TRANSACTION_CREATE_PAYMENT, transactionDTO, "transactionPayment" );
    }


    /**
     * Zentraler Kafka-Versand mit OpenTelemetry-Span.
     *
     * @param topic     Ziel-Topic
     * @param payload   Event-Inhalt (DTO oder String)
     * @param operation Name der Aktion, z.B. 'createAccount'
     */
    private void sendKafkaEvent(String topic, Object payload, String operation) {
        Span kafkaSpan = tracer.spanBuilder(String.format("kafka-publisher.%s", topic))
            .setParent(Context.current())
            .setAttribute("messaging.system", "kafka")
            .setAttribute("messaging.destination", topic)
            .setAttribute("messaging.destination_kind", "topic")
            .setAttribute("messaging.operation", operation)
            .startSpan();
        try (Scope scope = kafkaSpan.makeCurrent()) {
            SpanContext spanContext = kafkaSpan.getSpanContext();
            //SpanContext spanContext = Span.current().getSpanContext();

            Headers headers = kafkaUtilService.buildStandardHeaders(topic, operation, spanContext);

            ProducerRecord<String, Object> record = new ProducerRecord<>(topic, null, null, null, payload, headers);
            kafkaTemplate.send(record);

            kafkaSpan.setAttribute("messaging.kafka.message_type", payload.getClass().getSimpleName());
        } catch (Exception e) {
            kafkaSpan.recordException(e);
            kafkaSpan.setStatus(StatusCode.ERROR, "Kafka send failed");
            log.error("❌ Kafka send failed: topic={}, payload={}", topic, payload, e);
        } finally {
            kafkaSpan.end();
        }

        log.info("📤 Kafka-Event '{}' an Topic '{}': {}", operation, topic, payload);
    }
}
