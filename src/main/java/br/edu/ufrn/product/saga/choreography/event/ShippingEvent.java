package br.edu.ufrn.product.saga.choreography.event;

public record ShippingEvent(
    EventType type,
    String orderId,
    String productId,
    Integer productQuantity
) implements Event{}
