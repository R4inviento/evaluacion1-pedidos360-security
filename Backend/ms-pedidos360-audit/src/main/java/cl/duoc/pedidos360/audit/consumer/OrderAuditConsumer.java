package cl.duoc.pedidos360.audit.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import cl.duoc.pedidos360.audit.entity.AuditEventEntity;
import cl.duoc.pedidos360.audit.service.AuditService;

@Component
public class OrderAuditConsumer {

    private static final Logger log =
        LoggerFactory.getLogger(
            OrderAuditConsumer.class
        );

    private final AuditService auditService;

    public OrderAuditConsumer(
            AuditService auditService) {

        this.auditService = auditService;
    }

    @KafkaListener(
        topics = "${audit.kafka.topic}"
    )
    public void consume(
            ConsumerRecord<String, String> record) {

        AuditEventEntity saved =
            auditService.register(record);

        log.info(
            "AUDIT GUARDADO: id={}, tipo={}, key={}, topic={}, partition={}, offset={}",
            saved.getId(),
            saved.getEventType(),
            saved.getEventKey(),
            saved.getTopic(),
            saved.getPartitionNumber(),
            saved.getKafkaOffset()
        );

        log.info(
            "Payload auditado: {}",
            saved.getPayload()
        );
    }
}