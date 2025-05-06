package com.gentlecorp.payment.models.mapper;

import com.gentlecorp.payment.models.entitys.Payment;
import com.gentlecorp.payment.models.inputs.CreatePaymentInput;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {
    Payment toPayment(CreatePaymentInput createPaymentInput);
}