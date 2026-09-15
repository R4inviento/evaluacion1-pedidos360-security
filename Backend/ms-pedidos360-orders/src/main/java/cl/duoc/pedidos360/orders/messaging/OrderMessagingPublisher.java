package cl.duoc.pedidos360.orders.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import cl.duoc.pedidos360.orders.config.KafkaConfig;
import cl.duoc.pedidos360.orders.config.RabbitMqConfig;
import cl.duoc.pedidos360.orders.entity.OrderEntity;

@Component
public class OrderMessagingPublisher {

    private static final Logger log =
        LoggerFactory.getLogger(OrderMessagingPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderMessagingPublisher(
            RabbitTemplate rabbitTemplate,
            KafkaTemplate<String, String> kafkaTemplate) {

        this.rabbitTemplate = rabbitTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreated(OrderEntity order) {

        String json = buildOrderCreatedJson(order);

        publishRabbitCommand(
            order.getId(),
            json
        );

        publishKafkaEvent(
            order.getId(),
            json
        );
    }

    private void publishRabbitCommand(
            Long orderId,
            String json) {

        try {

            rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE,
                RabbitMqConfig.NOTIFY_ROUTING_KEY,
                json
            );

            log.info(
                "RabbitMQ: comando ORDER_CREATED publicado para pedido {}",
                orderId
            );

        } catch (Exception ex) {

            log.error(
                "RabbitMQ: error publicando pedido {}",
                orderId,
                ex
            );
        }
    }

    private void publishKafkaEvent(
            Long orderId,
            String json) {

        try {

            kafkaTemplate
                .send(
                    KafkaConfig.ORDER_EVENTS_TOPIC,
                    String.valueOf(orderId),
                    json
                )
                .whenComplete(
                    (result, ex) -> {

                        if (ex == null) {

                            log.info(
                                "Kafka: evento ORDER_CREATED publicado para pedido {}",
                                orderId
                            );

                        } else {

                            log.error(
                                "Kafka: error publicando pedido {}",
                                orderId,
                                ex
                            );
                        }
                    }
                );

        } catch (Exception ex) {

            log.error(
                "Kafka: error iniciando publicacion del pedido {}",
                orderId,
                ex
            );
        }
    }

    private String buildOrderCreatedJson(
            OrderEntity order) {

        return "{"
            + "\"eventType\":\"ORDER_CREATED\","
            + "\"orderId\":" + order.getId() + ","
            + "\"customerId\":\"" + escape(order.getCustomerId()) + "\","
            + "\"description\":\"" + escape(order.getDescription()) + "\","
            + "\"total\":" + order.getTotal() + ","
            + "\"status\":\"" + order.getStatus().name() + "\","
            + "\"createdAt\":\"" + order.getCreatedAt() + "\""
            + "}";
    }

    private String escape(String value) {

        if (value == null) {
            return "";
        }

        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r");
    }
}