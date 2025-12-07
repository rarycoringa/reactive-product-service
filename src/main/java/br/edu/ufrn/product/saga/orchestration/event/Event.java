package br.edu.ufrn.product.saga.orchestration.event;

public record Event(
    EventType type,
    String orderId,
    String productId,
    String productName,
    Integer productQuantity,
    Double productPrice
) {}
