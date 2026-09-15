package cl.duoc.pedidos360.report.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.pedidos360.report.entity.ReportOrderEventEntity;
import cl.duoc.pedidos360.report.repository.ReportOrderEventRepository;

@Service
public class ReportService {

    private static final Pattern EVENT_TYPE_PATTERN =
        Pattern.compile("\"eventType\"\\s*:\\s*\"([^\"]+)\"");

    private static final Pattern ORDER_ID_PATTERN =
        Pattern.compile("\"orderId\"\\s*:\\s*(\\d+)");

    private static final Pattern TOTAL_PATTERN =
        Pattern.compile("\"total\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)");

    private static final Pattern STATUS_PATTERN =
        Pattern.compile("\"status\"\\s*:\\s*\"([^\"]+)\"");

    private final ReportOrderEventRepository repository;

    public ReportService(
            ReportOrderEventRepository repository) {

        this.repository = repository;
    }

    @Transactional
    public ReportOrderEventEntity register(
            ConsumerRecord<String, String> record) {

        ReportOrderEventEntity event =
            new ReportOrderEventEntity();

        String payload = record.value();

        event.setEventType(
            extractString(
                EVENT_TYPE_PATTERN,
                payload,
                "UNKNOWN"
            )
        );

        event.setOrderId(
            extractLong(
                ORDER_ID_PATTERN,
                payload
            )
        );

        event.setTotal(
            extractDecimal(
                TOTAL_PATTERN,
                payload
            )
        );

        event.setStatus(
            extractString(
                STATUS_PATTERN,
                payload,
                "UNKNOWN"
            )
        );

        event.setTopic(
            record.topic()
        );

        event.setKafkaOffset(
            record.offset()
        );

        event.setPayload(
            payload
        );

        return repository.save(event);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSummary() {

        long totalEventos =
            repository.count();

        BigDecimal montoTotal =
            repository.findAll()
                .stream()
                .map(ReportOrderEventEntity::getTotal)
                .filter(value -> value != null)
                .reduce(
                    BigDecimal.ZERO,
                    BigDecimal::add
                );

        ReportOrderEventEntity ultimo =
            repository
                .findTopByOrderByReceivedAtDesc()
                .orElse(null);

        Map<String, Object> response =
            new LinkedHashMap<>();

        response.put(
            "totalEventos",
            totalEventos
        );

        response.put(
            "montoTotal",
            montoTotal
        );

        if (ultimo != null) {
            response.put(
                "ultimoPedidoId",
                ultimo.getOrderId()
            );

            response.put(
                "ultimoEstado",
                ultimo.getStatus()
            );

            response.put(
                "ultimoEvento",
                ultimo.getEventType()
            );

            response.put(
                "ultimaRecepcion",
                ultimo.getReceivedAt()
            );
        } else {
            response.put(
                "ultimoPedidoId",
                null
            );

            response.put(
                "ultimoEstado",
                null
            );

            response.put(
                "ultimoEvento",
                null
            );

            response.put(
                "ultimaRecepcion",
                null
            );
        }

        return response;
    }

    private String extractString(
            Pattern pattern,
            String payload,
            String defaultValue) {

        if (payload == null) {
            return defaultValue;
        }

        Matcher matcher =
            pattern.matcher(payload);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return defaultValue;
    }

    private Long extractLong(
            Pattern pattern,
            String payload) {

        if (payload == null) {
            return null;
        }

        Matcher matcher =
            pattern.matcher(payload);

        if (matcher.find()) {
            return Long.valueOf(
                matcher.group(1)
            );
        }

        return null;
    }

    private BigDecimal extractDecimal(
            Pattern pattern,
            String payload) {

        if (payload == null) {
            return BigDecimal.ZERO;
        }

        Matcher matcher =
            pattern.matcher(payload);

        if (matcher.find()) {
            return new BigDecimal(
                matcher.group(1)
            );
        }

        return BigDecimal.ZERO;
    }
}