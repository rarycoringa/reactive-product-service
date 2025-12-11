package br.edu.ufrn.product.saga.choreography.event;

public record ShippingEvent(
    EventType type
) implements Event{}
