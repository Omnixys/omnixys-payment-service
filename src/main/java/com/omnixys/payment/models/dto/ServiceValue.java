package com.omnixys.payment.models.dto;

public record ServiceValue(
    String schema,
    String host,
    int port
) {
}
