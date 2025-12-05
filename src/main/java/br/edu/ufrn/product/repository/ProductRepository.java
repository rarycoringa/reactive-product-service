package br.edu.ufrn.product.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import br.edu.ufrn.product.model.Product;

@Repository
public interface ProductRepository extends ReactiveMongoRepository <Product, String> {
    
}
