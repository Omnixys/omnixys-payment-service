package com.gentlecorp.payment.tracing;

import com.gentlecorp.payment.config.AppProperties;
import com.gentlecorp.payment.messaging.KafkaPublisherService;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoggerPlusFactory {

    private final KafkaPublisherService kafkaPublisherService;
    private final AppProperties appProperties;

    public LoggerPlus getLogger(Class<?> clazz) {
        return new LoggerPlus(
            LoggerFactory.getLogger(clazz),
            appProperties.getName(),
            kafkaPublisherService,
            clazz
        );
    }
}
