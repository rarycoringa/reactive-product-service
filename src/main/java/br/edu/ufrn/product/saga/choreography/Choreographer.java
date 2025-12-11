package br.edu.ufrn.product.saga.choreography;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import br.edu.ufrn.product.saga.choreography.event.EventType;
import br.edu.ufrn.product.saga.choreography.event.OrderEvent;
import br.edu.ufrn.product.saga.choreography.event.PaymentEvent;
import br.edu.ufrn.product.saga.choreography.event.ProductEvent;
import br.edu.ufrn.product.saga.choreography.event.ShippingEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Configuration
@Profile("choreography")
public class Choreographer {
    private static final Logger logger = LoggerFactory.getLogger(Choreographer.class);

    @Bean
    public Function<Flux<OrderEvent>, Flux<ProductEvent>> processOrderEvent() {
        return flux -> flux
            .doOnNext(event -> logger.info("Received order event: {}", event))
            .concatMap(this::handleOrderEvent)
            .concatMap(this::process)
            .doOnNext(event -> logger.info("Sending product event: {}", event));
    }

    @Bean
    public Function<Flux<PaymentEvent>, Flux<ProductEvent>> processPaymentEvent() {
        return flux -> flux
            .doOnNext(event -> logger.info("Received payment event: {}", event))
            .concatMap(this::handlePaymentEvent)
            .concatMap(this::process)
            .doOnNext(event -> logger.info("Sending product event: {}", event));
    }

    @Bean
    public Function<Flux<ShippingEvent>, Flux<ProductEvent>> processShippingEvent() {
        return flux -> flux
            .doOnNext(event -> logger.info("Received shipping event: {}", event))
            .concatMap(this::handleShippingEvent)
            .concatMap(this::process)
            .doOnNext(event -> logger.info("Sending product event: {}", event));
    }

    private Mono<ProductEvent> handleOrderEvent(OrderEvent event) {
        return switch (event.type()) {
            case ORDER_CREATED -> Mono.just(new ProductEvent(EventType.PRODUCT_RESERVED));
            case ORDER_CANCELLED, ORDER_FINISHED -> Mono.empty();
            default -> Mono.empty();
        };
    }

    private Mono<ProductEvent> handlePaymentEvent(PaymentEvent event) {
        return switch (event.type()) {
            case PAYMENT_REFUSED -> Mono.just(new ProductEvent(EventType.PRODUCT_RETURNED));
            case PAYMENT_CHARGED, PAYMENT_REFUNDED -> Mono.empty();
            default -> Mono.empty();
        };
    }

    private Mono<ProductEvent> handleShippingEvent(ShippingEvent event) {
        return switch (event.type()) {
            case SHIPPING_REFUSED -> Mono.just(new ProductEvent(EventType.PRODUCT_RETURNED));
            case SHIPPING_ACCEPTED -> Mono.empty();
            default -> Mono.empty();
        };
    }

    private Mono<ProductEvent> process(ProductEvent event) {
        return switch (event.type()) {
            case PRODUCT_RESERVED -> Mono.just(event)
                .doOnNext(e -> logger.info("Product reserved: {}", e));
                
            case PRODUCT_UNAVAILABLE -> Mono.just(event)
                .doOnNext(e -> logger.info("Product unavailable: {}", e));
                
            case PRODUCT_RETURNED -> Mono.just(event)
                .doOnNext(e -> logger.info("Product returned: {}", e));
                
            default -> Mono.empty();
        };
    }
}
