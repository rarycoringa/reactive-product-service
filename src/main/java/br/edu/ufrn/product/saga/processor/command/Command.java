package br.edu.ufrn.product.saga.orchestration.command;

public record Command(
    CommandType type,
    String orderId,
    String productId,
    Integer productQuantity
) {}
