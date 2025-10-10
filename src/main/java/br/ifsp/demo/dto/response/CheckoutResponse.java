package br.ifsp.demo.dto.response;

public record CheckoutResponse(
        String reservationId,
        double totalAmount,
        String status
) {}
