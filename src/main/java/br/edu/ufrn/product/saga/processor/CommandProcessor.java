package br.edu.ufrn.product.saga.processor;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import br.edu.ufrn.product.saga.processor.command.Command;
import br.edu.ufrn.product.saga.processor.event.Event;
import br.edu.ufrn.product.saga.processor.event.EventType;
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
            .doOnNext(command -> logger.info("Received payment command: {}", command))
            .concatMap(this::process)
            .doOnNext(event -> logger.info("Sending payment event: {}", event));

    }

    private Mono<Event> process(Command command) {
        return switch (command.type()) {
            case RESERVE_PRODUCT -> productService.decrease(command.productId(),  command.productQuantity())
                .flatMap(i -> productService.retrieveProduct(command.productId()))
                .map(product -> new Event(
                    EventType.PRODUCT_RESERVED,
                    command.orderId(),
                    product.id(),
                    product.name(),
                    command.productQuantity(),
                    product.price()))
                .onErrorReturn(new Event(
                    EventType.PRODUCT_UNAVAILABLE,
                    command.orderId(),
                    command.productId(),
                    null,
                    command.productQuantity(),
                    null));

            case RETURN_PRODUCT -> productService.increase(command.productId(), command.productQuantity())
                .flatMap(i -> productService.retrieveProduct(command.productId()))
                .map(product -> new Event(
                    EventType.PRODUCT_RETURNED,
                    command.orderId(),
                    product.id(),
                    product.name(),
                    command.productQuantity(),
                    product.price()));
        };
    }

}
