package com.omnixys.payment.messaging;

import lombok.RequiredArgsConstructor;

/**
 * Zentrale Konfiguration der Kafka-Topic-Namen.
 * <p>
 * Die Namen folgen dem Schema: {@code <service>.<entities>.<event>}.
 * </p>
 *
 * @author Caleb
 * @since 20.04.2025
 */
@RequiredArgsConstructor
public final class KafkaTopicProperties {

    public static final String TOPIC_INVOICE_CREATE_PAYMENT = "invoice.create.payment";

    public static final String TOPIC_NOTIFICATION_CREATE_PAYMENT = "notification.create.payment";

    public static final String TOPIC_TRANSACTION_CREATE_PAYMENT = "transaction.create.payment";

    public static final String TOPIC_LOG_STREAM_LOG_PAYMENT = "log-Stream.log.payment";

    public static final String TOPIC_PAYMENT_SHUTDOWN_ORCHESTRATOR = "PAYMENT.shutdown.orchestrator";
    public static final String TOPIC_PAYMENT_START_ORCHESTRATOR = "PAYMENT.start.orchestrator";
    public static final String TOPIC_PAYMENT_RESTART_ORCHESTRATOR = "PAYMENT.restart.orchestrator";

    public static final String TOPIC_ALL_SHUTDOWN_ORCHESTRATOR = "all.shutdown.orchestrator";
    public static final String TOPIC_ALL_START_ORCHESTRATOR = "all.start.orchestrator";
    public static final String TOPIC_ALL_RESTART_ORCHESTRATOR = "all.restart.orchestrator";
    
}
