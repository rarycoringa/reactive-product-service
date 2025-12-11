package br.edu.ufrn.product.saga.choreography.event;

public sealed interface Event permits OrderEvent, ProductEvent, PaymentEvent, ShippingEvent {
    EventType type();
    String orderId();
    String productId();
    Integer productQuantity();
}
