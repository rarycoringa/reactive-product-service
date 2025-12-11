package br.edu.ufrn.product.saga.choreography.event;

public record ProductEvent(
    EventType type
) implements Event{}
