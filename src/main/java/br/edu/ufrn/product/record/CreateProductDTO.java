package br.edu.ufrn.product.record;

public record CreateProductDTO(
    String name,
    Integer quantity,
    Double price
) {}
