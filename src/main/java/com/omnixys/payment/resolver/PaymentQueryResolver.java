package com.omnixys.payment.resolver;

import com.omnixys.payment.models.entitys.Payment;
import com.omnixys.payment.models.inputs.SearchCriteria;
import com.omnixys.payment.security.CustomUserDetails;
import com.omnixys.payment.service.PaymentReadService;
import com.omnixys.payment.tracing.LoggerPlus;
import com.omnixys.payment.tracing.LoggerPlusFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyMap;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaymentQueryResolver {
    private final PaymentReadService paymentReadService;
    private final LoggerPlusFactory factory;
    private LoggerPlus logger() {
        return factory.getLogger(getClass());
    }

    @QueryMapping("payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'SUPREME', 'ELITE', 'BASIC')")
    Payment getById(
        @Argument("id") final UUID id,
        final Authentication authentication
    ) {
        logger().debug("getById: id={}", id);

        final var user = (CustomUserDetails) authentication.getPrincipal();
        final var payment = paymentReadService.findById(id, user);

        logger().debug("getById: payment={}", payment);
        return payment;
    }

    @QueryMapping("payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    Collection<Payment> getPayments(
        @Argument final Optional<SearchCriteria> input,
        final Authentication authentication
    ) {
        logger().debug("getPayments: inputs={}", input);
        final var user = (CustomUserDetails) authentication.getPrincipal();
        final var searchCriteria = input.map(SearchCriteria::toMap).orElse(emptyMap());
        final var payments = paymentReadService.find(searchCriteria, user);
        logger().debug("getPayments: Payments={}", payments);
        return payments;
    }

    @QueryMapping("paymentsByPerson")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'SUPREME', 'ELITE', 'BASIC')")
    Collection<Payment> getByUser(
        @Argument final Optional<SearchCriteria> input,
        final Authentication authentication
    ) {
        final var user = (CustomUserDetails) authentication.getPrincipal();
        final var searchCriteria = input.map(SearchCriteria::toMap).orElse(emptyMap());
        logger().debug("getByUser: user={} searchCriteria={}", user, searchCriteria);
        final var payment = paymentReadService.findByUser(user,searchCriteria);

        logger().debug("getByUser: payment={}", payment);
        return payment;
    }

}
