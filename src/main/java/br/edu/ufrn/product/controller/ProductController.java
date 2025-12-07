package br.edu.ufrn.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ufrn.product.record.CreateProductRequestDTO;
import br.edu.ufrn.product.record.ProductResponseDTO;
import br.edu.ufrn.product.service.ProductService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;

        @PostMapping
    public Mono<ProductResponseDTO> createProduct(@RequestBody CreateProductRequestDTO body) {
        return productService.createProduct(body);
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProductResponseDTO> retrieveProducts() {
        return productService.retrieveProducts();
    }

    @GetMapping("/{id}")
    public Mono<ProductResponseDTO> retrieveProduct(@PathVariable String id) {
        return productService.retrieveProduct(id);
    }

}
