package com.gentlecorp.payment.service;

import com.gentlecorp.payment.exception.NotFoundException;
import com.gentlecorp.payment.models.dto.PaymentDTO;
import com.gentlecorp.payment.tracing.LoggerPlus;
import com.gentlecorp.payment.tracing.LoggerPlusFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.graphql.client.FieldAccessException;
import org.springframework.graphql.client.GraphQlTransportException;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@Transactional(readOnly = true)
public class InvoiceWriteService {

    private final HttpGraphQlClient graphQlClient;
    private final LoggerPlus logger;

    public InvoiceWriteService(@Qualifier("invoiceGraphQlClient") HttpGraphQlClient graphQlClient, LoggerPlusFactory logger) {
        this.graphQlClient = graphQlClient;
        this.logger = logger.getLogger(getClass());
    }

    public BigDecimal makePayment(final PaymentDTO paymentDTO, final String token) {
        logger.info("Making payment {}", paymentDTO);

        final var bearerToken = String.format("bearer %s", token);

        final var query = """
            mutation CreateInvoice($input: PaymentDTO!) {
                makePayment(
                    input: $input
                )
            }
        """;

        final Map<String, Object> variables = Map.of(
            "input", paymentDTO
        );

        final BigDecimal paidAmount;
        try {
            paidAmount = graphQlClient
                .mutate()
                .header(AUTHORIZATION, bearerToken)
                .build()
                .document(query)
                .variables(variables)
                .retrieveSync("makePayment")
                .toEntity(BigDecimal.class);
        } catch (final FieldAccessException | GraphQlTransportException ex) {
            logger.debug("makePayment", ex);
            throw new NotFoundException();
        }
        logger.debug("makePayment: paidAmount={}", paidAmount);
        return  paidAmount;
    }
}
