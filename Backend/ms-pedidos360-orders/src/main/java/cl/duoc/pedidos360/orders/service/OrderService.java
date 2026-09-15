package cl.duoc.pedidos360.orders.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.pedidos360.orders.dto.OrderRequest;
import cl.duoc.pedidos360.orders.entity.OrderEntity;
import cl.duoc.pedidos360.orders.entity.OrderStatus;
import cl.duoc.pedidos360.orders.exception.OrderNotFoundException;
import cl.duoc.pedidos360.orders.messaging.OrderMessagingPublisher;
import cl.duoc.pedidos360.orders.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final OrderMessagingPublisher messagingPublisher;

    public OrderService(
            OrderRepository repository,
            OrderMessagingPublisher messagingPublisher) {

        this.repository = repository;
        this.messagingPublisher =
            messagingPublisher;
    }

    @Transactional(readOnly = true)
    public List<OrderEntity> findAll() {

        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public OrderEntity findById(Long id) {

        return repository
            .findById(id)
            .orElseThrow(
                () -> new OrderNotFoundException(id)
            );
    }

    @Transactional(readOnly = true)
    public List<OrderEntity> findByCustomer(
            String customerId) {

        return repository
            .findByCustomerIdOrderByCreatedAtDesc(
                customerId
            );
    }

    @Transactional
    public OrderEntity create(
            OrderRequest request) {

        OrderEntity order =
            new OrderEntity();

        order.setCustomerId(
            request.getCustomerId()
        );

        order.setDescription(
            request.getDescription()
        );

        order.setTotal(
            request.getTotal()
        );

        order.setStatus(
            OrderStatus.CREATED
        );

        OrderEntity savedOrder =
            repository.saveAndFlush(order);

        messagingPublisher
            .publishOrderCreated(
                savedOrder
            );

        return savedOrder;
    }

    @Transactional
    public OrderEntity update(
            Long id,
            OrderRequest request) {

        OrderEntity order =
            findById(id);

        order.setCustomerId(
            request.getCustomerId()
        );

        order.setDescription(
            request.getDescription()
        );

        order.setTotal(
            request.getTotal()
        );

        return repository.save(order);
    }

    @Transactional
    public OrderEntity changeStatus(
            Long id,
            OrderStatus newStatus) {

        OrderEntity order =
            findById(id);

        validateTransition(
            order.getStatus(),
            newStatus
        );

        order.setStatus(
            newStatus
        );

        return repository.save(order);
    }

    @Transactional
    public void delete(Long id) {

        OrderEntity order =
            findById(id);

        repository.delete(order);
    }

    private void validateTransition(
            OrderStatus current,
            OrderStatus next) {

        if (current == next) {
            return;
        }

        boolean valid =
            switch (current) {

                case CREATED ->
                    next == OrderStatus.ACCEPTED
                    ||
                    next == OrderStatus.CANCELED;

                case ACCEPTED ->
                    next == OrderStatus.IN_PREPARATION
                    ||
                    next == OrderStatus.CANCELED;

                case IN_PREPARATION ->
                    next == OrderStatus.DISPATCHED
                    ||
                    next == OrderStatus.CANCELED;

                case DISPATCHED ->
                    next == OrderStatus.DELIVERED;

                case DELIVERED,
                     CANCELED ->
                    false;
            };

        if (!valid) {

            throw new IllegalArgumentException(
                "Transicion de estado no permitida: "
                + current
                + " -> "
                + next
            );
        }
    }
}