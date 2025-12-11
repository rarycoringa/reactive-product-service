package br.edu.ufrn.product.saga.choreography.event;

public record PaymentEvent(
    EventType type
) implements Event{}
