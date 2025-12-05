package br.edu.ufrn.stock.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import br.edu.ufrn.stock.model.Stock;

@Repository
public interface StockRepository extends ReactiveMongoRepository <Stock, String> {
    
}
