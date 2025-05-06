package com.gentlecorp.payment.service;

import com.gentlecorp.payment.exception.InsufficientFundsException;
import com.gentlecorp.payment.messaging.KafkaPublisherService;
import com.gentlecorp.payment.models.dto.PaymentDTO;
import com.gentlecorp.payment.models.entitys.Payment;
import com.gentlecorp.payment.models.enums.PaymentStatus;
import com.gentlecorp.payment.models.inputs.CreatePaymentInput;
import com.gentlecorp.payment.models.mapper.PaymentMapper;
import com.gentlecorp.payment.repository.PaymentRepository;
import com.gentlecorp.payment.security.CustomUserDetails;
import com.gentlecorp.payment.tracing.LoggerPlus;
import com.gentlecorp.payment.tracing.LoggerPlusFactory;
import io.micrometer.observation.annotation.Observed;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service zur Verwaltung schreibender Operationen im Kontext von Zahlungen.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentWriteService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final AccountReadService accountReadService;
    private final InvoiceWriteService invoiceWriteService;
    private final KafkaPublisherService kafkaPublisherService;
    private final Tracer tracer;
    private final LoggerPlusFactory factory;
    private LoggerPlus logger() {
        return factory.getLogger(getClass());
    }

    /**
     * Erstellt eine neue Zahlung für den gegebenen Benutzer.
     *
     * @param input    Eingabedaten (z.B. über GraphQL)
     * @return gespeicherte Payment-Entität
     */
    @Observed(name = "payment-service.write.create")
    public @NonNull Payment create(@NonNull CreatePaymentInput input, final CustomUserDetails user) {
        Span serviceSpan = tracer.spanBuilder("payment-service.read.find-by-id").startSpan();
        try (Scope serviceScope = serviceSpan.makeCurrent()) {
            assert serviceScope != null;
            logger().debug("create: inputs={} username={}", input, user.getUsername());

            Payment payment = paymentMapper.toPayment(input);
            final var paidAmount = checkInvoiceAmountLeft(payment, user.getToken());

            payment.setAmount(paidAmount);
            payment.setUsername(user.getUsername());
            payment.setStatus(PaymentStatus.PENDING);

            checkBalance(payment, user.getToken());
            final var savedPayment = paymentRepository.save(payment);
            kafkaPublisherService.createInvoice(savedPayment);
            logger().debug("create: saved={}", savedPayment);
            return savedPayment;
        } catch (Exception e) {
            serviceSpan.recordException(e);
            serviceSpan.setAttribute("exception.class", e.getClass().getSimpleName());
            throw e;
        } finally {
            serviceSpan.end();
        }
    }

    private BigDecimal checkInvoiceAmountLeft(final Payment payment, final String token) {
        logger().debug("checkInvoiceAmountLeft: payment={}", payment);

        final var payments = paymentRepository.findByInvoiceId(payment.getInvoiceId());

        // Summe aller bisherigen Zahlungen (außer diesem neuen payment ggf.)
        final var alreadyPaid = payments.stream()
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        final var paymentDTO = new PaymentDTO(
            payment.getAmount(),
            alreadyPaid,
            payment.getInvoiceId()
        );

        // überprüft wie viel wirklich noch gezahlt werden muss so dass man nicht zuviel zahlt und giubt aus wie viel man wirklich zahlen muss
        final var paidAmount = invoiceWriteService.makePayment(paymentDTO, token);

        logger().debug("checkInvoiceAmountLeft: versucht zu zahlen={}, wirklich bezahlt={}", payment.getAmount(), paidAmount);
        return paidAmount;
    }
    
    private void checkBalance(@NonNull Payment payment, final String token) {
        logger().debug("checkBalance: payment={}", payment);

        final var senderAccount = accountReadService.findAccountById(payment.getAccountId(), token);

//    validation.validateCustomer(senderAccount.customerUsername(), jwt);
        final BigDecimal newSenderBalance = senderAccount.balance().subtract(payment.getAmount());
        if (newSenderBalance.compareTo(BigDecimal.ZERO) < 0) {
            logger().error("Insufficient funds for transaction. Sender balance: {}", newSenderBalance);
            throw new InsufficientFundsException();
        }
    }

    /**
     * Aktualisiert den Status einer bestehenden Zahlung.
     *
     * @param inputs ID & neuer Status
     * @return aktualisierte Zahlung
     */
//    @Observed(name = "update-payment-status")
//    public @NonNull Payment updateStatus(@NonNull UpdatePaymentStatusInput inputs) {
//        logger.debug("updateStatus: inputs={}", inputs);
//
//        Payment payment = paymentRepository.findById(inputs.id())
//            .orElseThrow(() -> new NotFoundException("Zahlung nicht gefunden: " + inputs.id()));
//
//        payment.setStatus(inputs.status());
//        Payment saved = paymentRepository.save(payment);
//
//        logger.debug("updateStatus: updated={}", saved);
//        return saved;
//    }
}
