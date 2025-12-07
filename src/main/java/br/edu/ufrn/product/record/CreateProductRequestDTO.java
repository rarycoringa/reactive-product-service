package br.edu.ufrn.product.record;

public record CreateProductRequestDTO(
    String name,
    Integer quantity,
    Double price
) {}
