package com.gentlecorp.payment.models.events;

import com.gentlecorp.payment.models.entitys.Payment;

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