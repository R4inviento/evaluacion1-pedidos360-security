package cl.duoc.pedidos360.audit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.pedidos360.audit.entity.AuditEventEntity;

public interface AuditEventRepository
        extends JpaRepository<AuditEventEntity, Long> {
}