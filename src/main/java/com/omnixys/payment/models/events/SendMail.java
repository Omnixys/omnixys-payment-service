package com.omnixys.payment.models.events;

import com.omnixys.payment.models.entitys.Payment;

import java.util.UUID;

public record SendMail(
    UUID id
) {
    public static SendMail fromEntity(final Payment payment) {
        return new SendMail(
            payment.getId()
        );
    }
}