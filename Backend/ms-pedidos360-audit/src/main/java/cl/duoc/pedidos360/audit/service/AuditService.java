package cl.duoc.pedidos360.audit.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.pedidos360.audit.entity.AuditEventEntity;
import cl.duoc.pedidos360.audit.repository.AuditEventRepository;

@Service
public class AuditService {

    private static final Pattern EVENT_TYPE_PATTERN =
        Pattern.compile("\"eventType\"\\s*:\\s*\"([^\"]+)\"");

    private final AuditEventRepository repository;

    public AuditService(
            AuditEventRepository repository) {

        this.repository = repository;
    }

    @Transactional
    public AuditEventEntity register(
            ConsumerRecord<String, String> record) {

        AuditEventEntity event =
            new AuditEventEntity();

        event.setEventType(
            extractEventType(record.value())
        );

        event.setEventKey(
            record.key()
        );

        event.setTopic(
            record.topic()
        );

        event.setPartitionNumber(
            record.partition()
        );

        event.setKafkaOffset(
            record.offset()
        );

        event.setPayload(
            record.value()
        );

        return repository.save(event);
    }

    private String extractEventType(
            String payload) {

        if (payload == null) {
            return "UNKNOWN";
        }

        Matcher matcher =
            EVENT_TYPE_PATTERN.matcher(payload);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "UNKNOWN";
    }
}