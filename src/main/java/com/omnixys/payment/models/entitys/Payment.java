package com.omnixys.payment.models.entitys;

import com.omnixys.payment.models.enums.CurrencyType;
import com.omnixys.payment.models.enums.PaymentMethod;
import com.omnixys.payment.models.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "payment")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Payment {

    // Eindeutige ID der Zahlung
    @Id
    @GeneratedValue
    private UUID id;

    // Benutzer, der die Zahlung getätigt hat
    @Column(nullable = false)
    private String username;

    /**
     * das Konto von dem das Geld kommt
     */
    @Column(nullable = false)
    private UUID accountId;

    /**
     * Betrag der Zahlung (muss > 0 sein).
     */
    @DecimalMin("0.01")
    @Column(nullable = false)
    private BigDecimal amount;

    // Währung der Zahlung
    @Enumerated(EnumType.STRING)
    private CurrencyType currency;

    // Status der Zahlung (z.B. PENDING, COMPLETED, FAILED)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    // Zahlungsmethode (z.B. Kreditkarte, PayPal, etc.)
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @CreationTimestamp
    private LocalDateTime created;

    private UUID invoiceId;
}
