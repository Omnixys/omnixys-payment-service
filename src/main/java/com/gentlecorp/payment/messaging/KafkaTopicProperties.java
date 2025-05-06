package com.gentlecorp.payment.messaging;

/**
 * Zentrale Konfiguration der Kafka-Topic-Namen.
 * <p>
 * Die Namen folgen dem Schema: {@code <service>.<entities>.<event>}.
 * </p>
 *
 * @author Caleb
 * @since 20.04.2025
 */
public final class KafkaTopicProperties {

    private KafkaTopicProperties() {
        // Utility class – private Konstruktor verhindert Instanziierung
    }

    public static final String TOPIC_INVOICE = "newPaymentId";
    /** ✉️ Mailversand bei Kundenregistrierung */
    public static final String TOPIC_NOTIFICATION_ACCOUNT_CREATED = "notification.account.created";

    /** ✉️ Kundenkonto wurde erstellt (Eingang) */
    public static final String TOPIC_CUSTOMER_CREATED = "account.customer.created";

    public static final String TOPIC_CUSTOMER_DELETED = "account.customer.deleted";


    public static final String TOPIC_ACTIVITY_EVENTS = "activity.account.log";

    public static final String TOPIC_SYSTEM_SHUTDOWN = "system.shutdown";
}
