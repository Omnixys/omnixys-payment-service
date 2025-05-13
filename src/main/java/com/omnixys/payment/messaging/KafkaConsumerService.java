package com.omnixys.payment.messaging;

import com.omnixys.payment.tracing.LoggerPlus;
import com.omnixys.payment.tracing.LoggerPlusFactory;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.omnixys.payment.messaging.KafkaTopicProperties.TOPIC_ALL_RESTART_ORCHESTRATOR;
import static com.omnixys.payment.messaging.KafkaTopicProperties.TOPIC_ALL_SHUTDOWN_ORCHESTRATOR;
import static com.omnixys.payment.messaging.KafkaTopicProperties.TOPIC_ALL_START_ORCHESTRATOR;
import static com.omnixys.payment.messaging.KafkaTopicProperties.TOPIC_PAYMENT_RESTART_ORCHESTRATOR;
import static com.omnixys.payment.messaging.KafkaTopicProperties.TOPIC_PAYMENT_SHUTDOWN_ORCHESTRATOR;
import static com.omnixys.payment.messaging.KafkaTopicProperties.TOPIC_PAYMENT_START_ORCHESTRATOR;

/**
 * Kafka-Consumer für eingehende Events zur Kontoerstellung und -löschung.
 *
 * <p>
 * Unterstützt folgende Nachrichtenformate:
 * </p>
 *
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @since 05.05.2025
 */
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final ApplicationContext context;
    private final LoggerPlusFactory factory;
    private LoggerPlus logger() {
        return factory.getLogger(getClass());
    }

    @Observed(name = "kafka-consume.payment.orchestration")
    @KafkaListener(
        topics = {
            TOPIC_PAYMENT_SHUTDOWN_ORCHESTRATOR,
            TOPIC_PAYMENT_START_ORCHESTRATOR,
            TOPIC_PAYMENT_RESTART_ORCHESTRATOR
        },
        groupId = "${app.groupId}"
    )
    public void handlePersonScoped(ConsumerRecord<String, String> record) {
        final String topic = record.topic();
        logger().info("Person-spezifisches Kommando empfangen: {}", topic);

        switch (topic) {
            case TOPIC_PAYMENT_SHUTDOWN_ORCHESTRATOR -> shutdown();
            case TOPIC_PAYMENT_RESTART_ORCHESTRATOR -> restart();
            case TOPIC_PAYMENT_START_ORCHESTRATOR -> logger().info("Startsignal für Person-Service empfangen");
        }
    }

    @Observed(name = "kafka-consume.all.orchestration")
    @KafkaListener(
        topics = {
            TOPIC_ALL_SHUTDOWN_ORCHESTRATOR,
            TOPIC_ALL_START_ORCHESTRATOR,
            TOPIC_ALL_RESTART_ORCHESTRATOR
        },
        groupId = "${app.groupId}"
    )
    public void handleGlobalScoped(ConsumerRecord<String, String> record) {
        final String topic = record.topic();
        logger().info("Globales Systemkommando empfangen: {}", topic);

        switch (topic) {
            case TOPIC_ALL_SHUTDOWN_ORCHESTRATOR -> shutdown();
            case TOPIC_ALL_RESTART_ORCHESTRATOR -> restart();
            case TOPIC_ALL_START_ORCHESTRATOR -> logger().info("Globales Startsignal empfangen");
        }
    }

    private void shutdown() {
        try {
            logger().info("→ Anwendung wird heruntergefahren (Shutdown-Kommando).");
            ((ConfigurableApplicationContext) context).close();
        } catch (Exception e) {
            logger().error("Fehler beim Shutdown: {}", e.getMessage(), e);
        }
    }


    private void restart() {
        logger().info("→ Anwendung wird neugestartet (Restart-Kommando).");
        ((ConfigurableApplicationContext) context).close();
        // Neustart durch externen Supervisor erwartet
    }
}
