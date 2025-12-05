package br.edu.ufrn.product.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import br.edu.ufrn.product.exception.InsufficientQuantityException;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Document(collation = "products")
public class Product {

    @Id
    private String id;

    @Indexed
    private String name;

    @PositiveOrZero(message = "Quantity should be zero or higher.")
    private Integer quantity;

    @PositiveOrZero(message = "Price should be zero or higher.")
    private Double price;

    @Indexed(direction = IndexDirection.DESCENDING)
    private Instant createdAt;

    public Product(String id, String name, Integer quantity, Double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.createdAt = Instant.now();
    }

    public void increase(@Positive Integer value) {
        quantity += value;
    }

    public void decrease(@Positive Integer value) throws InsufficientQuantityException {
        if (value > quantity) {
            throw new InsufficientQuantityException();
        }
        
        quantity -= value;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    
}
