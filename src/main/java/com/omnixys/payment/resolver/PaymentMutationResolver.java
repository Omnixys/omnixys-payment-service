package com.omnixys.payment.resolver;

import com.omnixys.payment.models.entitys.Payment;
import com.omnixys.payment.models.inputs.CreatePaymentInput;
import com.omnixys.payment.models.mapper.PaymentMapper;
import com.omnixys.payment.security.CustomUserDetails;
import com.omnixys.payment.service.PaymentWriteService;
import com.omnixys.payment.tracing.LoggerPlus;
import com.omnixys.payment.tracing.LoggerPlusFactory;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PaymentMutationResolver {

    private final PaymentWriteService paymentWriteService;
    private final Validator validator;
    private final LoggerPlusFactory factory;
    private LoggerPlus logger() {
        return factory.getLogger(getClass());
    }
    private final PaymentMapper paymentMapper;

    @MutationMapping("createPayment")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'SUPREME', 'ELITE', 'BASIC')")
    public UUID createPayment(
        @Argument("input") final CreatePaymentInput input,
        @Argument("receiver") final UUID receiver,
        final Authentication authentication
    ) {
        final var user = (CustomUserDetails) authentication.getPrincipal();
        logger().debug("createPayment: inputs={}, receiver={}, user={}", input, receiver, user);

        Payment payment = paymentMapper.toPayment(input);

        // Optional: validator.validate(inputs); für manuelle Validierung

        final var saved = paymentWriteService.create(payment, receiver, user);
        logger().debug("createPayment: saved={}", saved);
        return saved.getId();
    }
}
