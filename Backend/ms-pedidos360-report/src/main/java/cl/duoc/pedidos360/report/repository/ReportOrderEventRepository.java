package cl.duoc.pedidos360.report.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.pedidos360.report.entity.ReportOrderEventEntity;

public interface ReportOrderEventRepository
        extends JpaRepository<ReportOrderEventEntity, Long> {

    Optional<ReportOrderEventEntity>
        findTopByOrderByReceivedAtDesc();
}