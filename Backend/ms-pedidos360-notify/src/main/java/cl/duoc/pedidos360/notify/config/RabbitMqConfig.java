package cl.duoc.pedidos360.notify.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE =
        "pedidos360.commands";

    public static final String QUEUE =
        "notify.order-created";

    public static final String ROUTING_KEY =
        "notify.order-created";

    @Bean
    public TopicExchange pedidosExchange() {
        return new TopicExchange(
            EXCHANGE,
            true,
            false
        );
    }

    @Bean
    public Queue notifyQueue() {
        return new Queue(
            QUEUE,
            true
        );
    }

    @Bean
    public Binding notifyBinding(
            Queue notifyQueue,
            TopicExchange pedidosExchange) {

        return BindingBuilder
            .bind(notifyQueue)
            .to(pedidosExchange)
            .with(ROUTING_KEY);
    }
}