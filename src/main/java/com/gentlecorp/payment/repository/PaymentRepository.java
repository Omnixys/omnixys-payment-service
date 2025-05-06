package com.gentlecorp.payment.repository;

import com.gentlecorp.payment.models.entitys.Payment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {

    @Override
    @NonNull
    Optional<Payment> findById(@NonNull UUID uuid);

    @NonNull
    @Override
    List<Payment> findAll (Specification<Payment> spec);

    @Override
    @NonNull
    List<Payment> findAll();

    Optional<Payment> findByUsername(String username);
    Collection<Payment> findByInvoiceId(UUID invoiceId);

}