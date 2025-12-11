package br.edu.ufrn.product.saga.choreography.event;

public record OrderEvent(
    EventType type
) implements Event{}
