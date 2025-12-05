package br.edu.ufrn.product.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ufrn.product.exception.InsufficientQuantityException;
import br.edu.ufrn.product.exception.ProductNotFoundException;
import br.edu.ufrn.product.model.Product;
import br.edu.ufrn.product.repository.ProductRepository;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public Mono<Integer> increase(String id, Integer value) {
        return productRepository
            .findById(id)
            .switchIfEmpty(Mono.error(new ProductNotFoundException()))
            .flatMap(product -> {
                product.increase(value);
                return productRepository.save(product);
            })
            .map(Product::getQuantity)
            .doOnSuccess(quantity -> logger.info("Product successfully increased: id={}, quantity={}", id, quantity))
            .doOnError(err -> logger.error("Product increase failed: id={}, reason={}", id, err.getMessage()));
    }

    public Mono<Integer> decrease(String id, Integer value) {
        return productRepository
            .findById(id)
            .switchIfEmpty(Mono.error(new ProductNotFoundException()))
            .flatMap(product -> {
                try {
                    product.decrease(value);
                    return productRepository.save(product);
                } catch (InsufficientQuantityException e) {
                    return Mono.error(e);
                }
            })
            .map(Product::getQuantity)
            .doOnSuccess(quantity -> logger.info("Product successfully decreased: id={}, quantity={}", id, quantity))
            .doOnError(err -> logger.error("Product decrease failed: id={}, reason={}", id, err.getMessage()));
    }
    
}
