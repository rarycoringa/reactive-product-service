package br.edu.ufrn.product.saga.choreography;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import br.edu.ufrn.product.saga.choreography.event.EventType;
import br.edu.ufrn.product.saga.choreography.event.OrderEvent;
import br.edu.ufrn.product.saga.choreography.event.PaymentEvent;
import br.edu.ufrn.product.saga.choreography.event.ProductEvent;
import br.edu.ufrn.product.saga.choreography.event.ShippingEvent;
import br.edu.ufrn.product.service.ProductService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
@Profile("choreography")
public class Choreographer {

    private static final Logger logger = LoggerFactory.getLogger(Choreographer.class);

    @Autowired
    private ProductService productService;

    @Bean
    public Function<Flux<OrderEvent>, Flux<ProductEvent>> processOrderEvent() {
        return flux -> flux
            .doOnNext(event -> logger.info("Received order event: {}", event))
            .concatMap(this::process)
            .doOnNext(event -> logger.info("Sending product event: {}", event));
    }

    @Bean
    public Function<Flux<PaymentEvent>, Flux<ProductEvent>> processPaymentEvent() {
        return flux -> flux
            .doOnNext(event -> logger.info("Received payment event: {}", event))
            .concatMap(this::process)
            .doOnNext(event -> logger.info("Sending product event: {}", event));
    }

    @Bean
    public Function<Flux<ShippingEvent>, Flux<ProductEvent>> processShippingEvent() {
        return flux -> flux
            .doOnNext(event -> logger.info("Received shipping event: {}", event))
            .concatMap(this::process)
            .doOnNext(event -> logger.info("Sending product event: {}", event));
    }

    private Mono<ProductEvent> process(OrderEvent event) {
        return switch (event.type()) {
            case ORDER_CREATED -> productService.decrease(event.productId(), event.productQuantity())
                .flatMap(i -> productService.retrieveProduct(event.productId()))
                .map(product -> new ProductEvent(
                    EventType.PRODUCT_RESERVED,
                    event.orderId(),
                    event.productId(),
                    event.productQuantity()));

            case ORDER_CANCELLED, ORDER_FINISHED -> Mono.empty();

            default -> Mono.empty();
        };
    }
    
    private Mono<ProductEvent> process(PaymentEvent event) {
        return switch (event.type()) {
            case PAYMENT_REFUSED -> productService.increase(event.productId(), event.productQuantity())
                .flatMap(i -> productService.retrieveProduct(event.productId()))
                .map(product -> new ProductEvent(
                    EventType.PRODUCT_RETURNED,
                    event.orderId(),
                    event.productId(),
                    event.productQuantity()));

            case PAYMENT_CHARGED, PAYMENT_REFUNDED -> Mono.empty();

            default -> Mono.empty();
        };
    }

    private Mono<ProductEvent> process(ShippingEvent event) {
        return switch (event.type()) {
            case SHIPPING_REFUSED -> productService.increase(event.productId(), event.productQuantity())
                .flatMap(i -> productService.retrieveProduct(event.productId()))
                .map(product -> new ProductEvent(
                    EventType.PRODUCT_RETURNED,
                    event.orderId(),
                    event.productId(),
                    event.productQuantity()));

            case SHIPPING_ACCEPTED-> Mono.empty();

            default -> Mono.empty();
        };
    }

}
