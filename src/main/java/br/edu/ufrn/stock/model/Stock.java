package br.edu.ufrn.stock.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import br.edu.ufrn.stock.exception.InsufficientStockException;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Document(collation = "stocks")
public class Stock {

    @Id
    private String id;

    @Indexed
    private String itemId;

    @PositiveOrZero(message = "Quantity should be zero or higher.")
    private Integer quantity;

    @Indexed(direction = IndexDirection.DESCENDING)
    private Instant createdAt;

    public Stock(String itemId) {
        this.itemId = itemId;
        this.quantity = 0;
        
        this.createdAt = Instant.now();
    }

    public void increase(@Positive Integer value) {
        quantity += value;
    }

    public void decrease(@Positive Integer value) throws InsufficientStockException {
        if (value > quantity) {
            throw new InsufficientStockException();
        }
        
        quantity -= value;
    }

    public String getId() {
        return id;
    }

    public String getItemId() {
        return itemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    
}
