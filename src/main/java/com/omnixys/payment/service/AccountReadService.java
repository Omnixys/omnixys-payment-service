package com.omnixys.payment.service;

import com.omnixys.payment.exception.NotFoundException;
import com.omnixys.payment.models.dto.Account;
import com.omnixys.payment.tracing.LoggerPlus;
import com.omnixys.payment.tracing.LoggerPlusFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.graphql.client.FieldAccessException;
import org.springframework.graphql.client.GraphQlTransportException;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@Transactional(readOnly = true)
public class AccountReadService {

    private final HttpGraphQlClient graphQlClient;
    private final LoggerPlus logger;

    public AccountReadService(@Qualifier("accountGraphQlClient") HttpGraphQlClient graphQlClient, LoggerPlusFactory logger) {
        this.graphQlClient = graphQlClient;
        this.logger = logger.getLogger(getClass());
    }

    public Account findAccountById(final UUID id, final String token) {
        logger.debug("findAccountById: id={}", id);

        final var bearerToken = String.format("bearer %s", token);
        final var query = """
        query Account($id: ID!) {
            account(id: $id) {
                id
                balance
            }
        }
        """;

        final Map<String, Object> variables = Map.of(
            "id", id
        );

        final Account account;
        try {
            account = graphQlClient
                .mutate()
                .header(AUTHORIZATION, bearerToken)
                .build()
                .document(query)
                .variables(variables)
                .retrieveSync("account")
                .toEntity(Account.class);

            logger.debug("findAccountById: accountIdList={}", account);
        } catch (final FieldAccessException | GraphQlTransportException ex) {
            logger.debug("findAccountById", ex);
            throw new NotFoundException();
        }
        logger.debug("findAccountById: accountIdList={}", account);
        return  account;
    }
}
