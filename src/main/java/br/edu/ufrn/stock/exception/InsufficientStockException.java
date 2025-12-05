package br.edu.ufrn.stock.exception;

public class InsufficientStockException extends ArithmeticException {
    private static final String message = "Insufficient quantity in stock.";

    public InsufficientStockException() {
        super(message);
    }
}
