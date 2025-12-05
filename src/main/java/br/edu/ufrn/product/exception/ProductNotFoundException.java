package br.edu.ufrn.product.exception;

public class ProductNotFoundException extends RuntimeException {
    private static final String message = "Product not found.";

    public ProductNotFoundException() {
        super(message);
    }
}
