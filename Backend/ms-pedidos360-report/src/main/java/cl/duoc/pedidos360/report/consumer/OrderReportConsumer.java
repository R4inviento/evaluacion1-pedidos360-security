package cl.duoc.pedidos360.report.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import cl.duoc.pedidos360.report.entity.ReportOrderEventEntity;
import cl.duoc.pedidos360.report.service.ReportService;

@Component
public class OrderReportConsumer {

    private static final Logger log =
        LoggerFactory.getLogger(
            OrderReportConsumer.class
        );

    private final ReportService reportService;

    public OrderReportConsumer(
            ReportService reportService) {

        this.reportService = reportService;
    }

    @KafkaListener(
        topics = "${report.kafka.topic}"
    )
    public void consume(
            ConsumerRecord<String, String> record) {

        ReportOrderEventEntity saved =
            reportService.register(record);

        log.info(
            "REPORT GUARDADO: id={}, orderId={}, tipo={}, estado={}, total={}, offset={}",
            saved.getId(),
            saved.getOrderId(),
            saved.getEventType(),
            saved.getStatus(),
            saved.getTotal(),
            saved.getKafkaOffset()
        );
    }
}