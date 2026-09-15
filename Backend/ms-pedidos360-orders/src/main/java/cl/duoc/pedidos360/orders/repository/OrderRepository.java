package cl.duoc.pedidos360.orders.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.pedidos360.orders.entity.OrderEntity;

@Repository
public interface OrderRepository
        extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity>
        findByCustomerIdOrderByCreatedAtDesc(
            String customerId
        );
}