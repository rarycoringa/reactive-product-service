package br.edu.ufrn.product.saga.choreography.event;

public record OrderEvent(
    EventType type,
    String orderId,
    String productId,
    Integer productQuantity
) implements Event{}
