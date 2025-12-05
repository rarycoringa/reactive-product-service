package br.edu.ufrn.product.exception;

public class InsufficientQuantityException extends ArithmeticException {
    private static final String message = "Insufficient quantity in stock.";

    public InsufficientQuantityException() {
        super(message);
    }
}
