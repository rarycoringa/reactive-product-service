package br.edu.ufrn.product.saga.orchestration;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import br.edu.ufrn.product.exception.InsufficientQuantityException;
import br.edu.ufrn.product.saga.orchestration.command.Command;
import br.edu.ufrn.product.saga.orchestration.event.Event;
import br.edu.ufrn.product.saga.orchestration.event.EventType;
import br.edu.ufrn.product.service.ProductService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
@Profile("orchestration")
public class CommandProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CommandProcessor.class);

    @Autowired  
    private ProductService productService;
    
    @Bean
    public Function<Flux<Command>, Flux<Event>> processProductCommand() {
        return flux -> flux
            .concatMap(this::process);
    }

    private Mono<Event> process(Command command) {
        return switch (command.type()) {
            case RESERVE_PRODUCT -> reserveProduct(command);
            case RETURN_PRODUCT -> returnProduct(command);
        };
    }

    private Mono<Event> reserveProduct(Command command) {
        return productService
            .retrieveProduct(command.productId())
            .flatMap(product -> productService
                .decreaseProduct(command.productId(), command.productQuantity())
                .then(
                    Mono.just(
                        new Event(
                            EventType.PRODUCT_RESERVED,
                            command.orderId(),
                            product.id(),
                            product.name(),
                            command.productQuantity(),
                            product.price()
                        )
                    )
                )
                .onErrorResume(
                    InsufficientQuantityException.class, e -> Mono.just(
                        new Event(
                            EventType.PRODUCT_UNAVAILABLE,
                            command.orderId(),
                            product.id(),
                            product.name(),
                            command.productQuantity(),
                            product.price()
                        )
                    )
                )
            )
            .doOnSuccess(productEvent -> logger.info("Product reserved: {}", productEvent));
    }

    private Mono<Event> returnProduct(Command command) {
        return productService
            .retrieveProduct(command.productId())
            .flatMap(product -> productService
                .increaseProduct(command.productId(), command.productQuantity())
                .then(
                    Mono.just(
                        new Event(
                            EventType.PRODUCT_RETURNED,
                            command.orderId(),
                            product.id(),
                            product.name(),
                            command.productQuantity(),
                            product.price()
                        )
                    )
                )
            )
            .doOnSuccess(productEvent -> logger.info("Product returned: {}", productEvent));
    }

}
