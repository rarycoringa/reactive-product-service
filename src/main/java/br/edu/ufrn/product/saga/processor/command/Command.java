package br.edu.ufrn.product.saga.processor.command;

public record Command(
    CommandType type,
    String orderId,
    String productId,
    Integer productQuantity
) {}
