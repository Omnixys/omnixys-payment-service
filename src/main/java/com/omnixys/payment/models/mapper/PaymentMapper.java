package com.omnixys.payment.models.mapper;

import com.omnixys.payment.models.entitys.Payment;
import com.omnixys.payment.models.inputs.CreatePaymentInput;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {
    Payment toPayment(CreatePaymentInput createPaymentInput);
}