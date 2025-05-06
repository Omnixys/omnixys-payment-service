package com.gentlecorp.payment.resolver;

import com.gentlecorp.payment.models.inputs.CreatePaymentInput;
import com.gentlecorp.payment.security.CustomUserDetails;
import com.gentlecorp.payment.service.PaymentWriteService;
import com.gentlecorp.payment.tracing.LoggerPlus;
import com.gentlecorp.payment.tracing.LoggerPlusFactory;
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

    @MutationMapping("createPayment")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'SUPREME', 'ELITE', 'BASIC')")
    public UUID createPayment(
        @Argument("input") final CreatePaymentInput input,
        final Authentication authentication
    ) {
        final var user = (CustomUserDetails) authentication.getPrincipal();
        logger().debug("createPayment: inputs={} user={}", input, user);

        // Optional: validator.validate(inputs); für manuelle Validierung

//        final var payment = paymentMapper.toPayment(inputs);
//        payment.setUsername(user.getUsername());
        final var saved = paymentWriteService.create(input, user);
        logger().debug("createPayment: saved={}", saved);
        return saved.getId();
    }
}
