package com.gentlecorp.payment.service;

import com.gentlecorp.payment.exception.AccessForbiddenException;
import com.gentlecorp.payment.exception.NotFoundException;
import com.gentlecorp.payment.messaging.KafkaPublisherService;
import com.gentlecorp.payment.models.entitys.Payment;
import com.gentlecorp.payment.repository.PaymentRepository;
import com.gentlecorp.payment.repository.SpecificationBuilder;
import com.gentlecorp.payment.security.CustomUserDetails;
import com.gentlecorp.payment.security.enums.RoleType;
import com.gentlecorp.payment.tracing.LoggerPlus;
import com.gentlecorp.payment.tracing.LoggerPlusFactory;
import io.micrometer.observation.annotation.Observed;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.gentlecorp.payment.security.enums.RoleType.ADMIN;
import static com.gentlecorp.payment.security.enums.RoleType.USER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentReadService {
    private final PaymentRepository paymentRepository;
    private final SpecificationBuilder specificationBuilder;
    private final Tracer tracer;
    private final LoggerPlusFactory factory;
    private LoggerPlus logger() {
        return factory.getLogger(getClass());
    }

    @Observed(name = "payment-service.read.find-by-id")
    public @NonNull Payment findById(final UUID id, final CustomUserDetails user) {
        Span serviceSpan = tracer.spanBuilder("payment-service.read.find-by-id").startSpan();
        try (Scope serviceScope = serviceSpan.makeCurrent()) {
            assert serviceScope != null;
            logger().debug("findById: id={} user={}", id, user);
            final var payment = paymentRepository.findById(id).orElseThrow(NotFoundException::new);
            validateUserRole(user);
            logger().debug("findById: Payment={}", payment);
            return payment;
        } catch (Exception e) {
            serviceSpan.recordException(e);
            serviceSpan.setAttribute("exception.class", e.getClass().getSimpleName());
            throw e;
        } finally {
            serviceSpan.end();
        }
    }

    @Observed(name = "payment-service.read.find-by-id")
    public @NonNull Collection<Payment> find(final Map<String, List<Object>> searchCriteria, final UserDetails user) {
        Span serviceSpan = tracer.spanBuilder("payment-service.read.find-by-id").startSpan();
        try (Scope serviceScope = serviceSpan.makeCurrent()) {
            assert serviceScope != null;
            validateUserRole(user);

            if (searchCriteria.isEmpty()) {
                return paymentRepository.findAll();
            }

            final var specification = specificationBuilder
                .build(searchCriteria)
                .orElseThrow(() -> new NotFoundException(searchCriteria));
            final var payments = paymentRepository.findAll(specification);

            if (payments.isEmpty()) {
                throw new NotFoundException(searchCriteria);
            }

            logger().debug("find: payments={}", payments);
            return payments;
        } catch (Exception e) {
            serviceSpan.recordException(e);
            serviceSpan.setAttribute("exception.class", e.getClass().getSimpleName());
            throw e;
        } finally {
            serviceSpan.end();
        }
    }

    @Observed(name = "payment-service.read.find-by-id")
    public @NonNull Collection<Payment> findByUser(
        final CustomUserDetails user,
        final Map<String, List<Object>> searchCriteria
    ) {
        Span serviceSpan = tracer.spanBuilder("payment-service.read.find-by-id").startSpan();
        try (Scope serviceScope = serviceSpan.makeCurrent()) {
            assert serviceScope != null;
            logger().debug("findByUser: user={} criteria={}", user.getUsername(), searchCriteria);

            // Fix: neue Map erzeugen, damit put() erlaubt ist
            final var extendedCriteria = new HashMap<>(searchCriteria);
            extendedCriteria.put("username", List.of(user.getUsername()));

            final var specification = specificationBuilder
                .build(extendedCriteria)
                .orElseThrow(() -> new NotFoundException(extendedCriteria));

            final var payments = paymentRepository.findAll(specification);

            if (payments.isEmpty()) {
                throw new NotFoundException(extendedCriteria);
            }

            logger().debug("findByUser: filteredPayments={}", payments);
            return payments;
        } catch (Exception e) {
            serviceSpan.recordException(e);
            serviceSpan.setAttribute("exception.class", e.getClass().getSimpleName());
            throw e;
        } finally {
            serviceSpan.end();
        }
    }



    //TODO UtilService
    public void validateUserRole(UserDetails user) {
        final var roles = user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(str -> str.substring(RoleType.ROLE_PREFIX.length()))
            .map(RoleType::valueOf)
            .collect(Collectors.toSet());

        if (!roles.contains(ADMIN) && !roles.contains(USER)) {
            throw new AccessForbiddenException(user.getUsername(), roles);
        }
    }
}
