package cl.duoc.pedidos360.notify.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import cl.duoc.pedidos360.notify.config.RabbitMqConfig;

@Component
public class OrderNotificationConsumer {

    private static final Logger log =
        LoggerFactory.getLogger(
            OrderNotificationConsumer.class
        );

    @RabbitListener(queues = RabbitMqConfig.QUEUE)
    public void processOrderCreated(String message) {

        log.info("========================================");
        log.info("NOTIFICACION PEDIDOS360 RECIBIDA");
        log.info("Mensaje: {}", message);
        log.info("Notificacion simulada procesada correctamente");
        log.info("========================================");
    }
}