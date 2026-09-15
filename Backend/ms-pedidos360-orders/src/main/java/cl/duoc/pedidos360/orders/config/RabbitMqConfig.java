package cl.duoc.pedidos360.orders.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "pedidos360.commands";
    public static final String NOTIFY_QUEUE = "notify.order-created";
    public static final String NOTIFY_ROUTING_KEY = "notify.order-created";

    @Bean
    public TopicExchange pedidosExchange() {
        return new TopicExchange(
            EXCHANGE,
            true,
            false
        );
    }

    @Bean
    public Queue notifyOrderCreatedQueue() {
        return new Queue(
            NOTIFY_QUEUE,
            true
        );
    }

    @Bean
    public Binding notifyOrderCreatedBinding(
            Queue notifyOrderCreatedQueue,
            TopicExchange pedidosExchange) {

        return BindingBuilder
            .bind(notifyOrderCreatedQueue)
            .to(pedidosExchange)
            .with(NOTIFY_ROUTING_KEY);
    }
}