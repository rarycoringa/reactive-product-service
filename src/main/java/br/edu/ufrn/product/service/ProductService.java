package br.edu.ufrn.product.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ufrn.product.exception.InsufficientQuantityException;
import br.edu.ufrn.product.exception.ProductNotFoundException;
import br.edu.ufrn.product.model.Product;
import br.edu.ufrn.product.record.CreateProductDTO;
import br.edu.ufrn.product.record.ProductDTO;
import br.edu.ufrn.product.repository.ProductRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public Mono<ProductDTO> createProduct(CreateProductDTO productRequest) {
        return productRepository
            .save(new Product(
                productRequest.name(),
                productRequest.quantity(),
                productRequest.price()))
            .map(product -> new ProductDTO(
                product.getId(),
                product.getName(),
                product.getQuantity(),
                product.getPrice(),
                product.getCreatedAt()))
            .doOnSuccess(product -> logger.info("Product successfully created: id={}", product.id()));
    }

    public Flux<ProductDTO> retrieveProducts() {
        return productRepository
            .findAll()
            .map(product -> new ProductDTO(
                product.getId(),
                product.getName(),
                product.getQuantity(),
                product.getPrice(),
                product.getCreatedAt()))
            .doOnNext(product -> logger.info("Product successfully retrieved: id={}", product.id()));
    }

    public Mono<ProductDTO> retrieveProduct(String productId) {
        return productRepository
            .findById(productId)
            .map(product -> new ProductDTO(
                product.getId(),
                product.getName(),
                product.getQuantity(),
                product.getPrice(),
                product.getCreatedAt()))
            .doOnSuccess(product -> logger.info("Order successfully retrieved: id={}", product.id()));
    }

    public Mono<Integer> increaseProduct(String id, Integer value) {
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

    public Mono<Integer> decreaseProduct(String id, Integer value) {
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
