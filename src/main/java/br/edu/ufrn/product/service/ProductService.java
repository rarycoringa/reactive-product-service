package br.edu.ufrn.product.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ufrn.product.model.Product;
import br.edu.ufrn.product.record.CreateProductDTO;
import br.edu.ufrn.product.record.ProductDTO;
import br.edu.ufrn.product.repository.ProductRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    @Autowired
    private ProductRepository productRepository;

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
                product.getCreatedAt()));
    }

    public Flux<ProductDTO> retrieveProducts() {
        return productRepository
            .findAll()
            .map(product -> new ProductDTO(
                product.getId(),
                product.getName(),
                product.getQuantity(),
                product.getPrice(),
                product.getCreatedAt()));
    }

    public Mono<ProductDTO> retrieveProduct(String productId) {
        return productRepository
            .findById(productId)
            .map(product -> new ProductDTO(
                product.getId(),
                product.getName(),
                product.getQuantity(),
                product.getPrice(),
                product.getCreatedAt()));
    }

    public Mono<Integer> decrease(String id, Integer value) {
        return productRepository
            .findById(id)
            .doOnNext(product -> product.decrease(value))
            .flatMap(productRepository::save)
            .map(Product::getQuantity);
    }

    public Mono<Integer> increase(String id, Integer value) {
        return productRepository
            .findById(id)
            .doOnNext(product -> product.increase(value))
            .flatMap(productRepository::save)
            .map(Product::getQuantity);
    }
    
}
