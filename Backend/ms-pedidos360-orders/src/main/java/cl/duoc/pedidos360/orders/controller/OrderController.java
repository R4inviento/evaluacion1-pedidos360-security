package cl.duoc.pedidos360.orders.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.pedidos360.orders.dto.OrderRequest;
import cl.duoc.pedidos360.orders.dto.OrderStatusRequest;
import cl.duoc.pedidos360.orders.entity.OrderEntity;
import cl.duoc.pedidos360.orders.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public List<OrderEntity> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public OrderEntity findById(
            @PathVariable Long id) {

        return service.findById(id);
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderEntity> findByCustomer(
            @PathVariable String customerId) {

        return service.findByCustomer(customerId);
    }

    @PostMapping
    public ResponseEntity<OrderEntity> create(
            @Valid
            @RequestBody
            OrderRequest request) {

        OrderEntity created =
            service.create(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(created);
    }

    @PutMapping("/{id}")
    public OrderEntity update(
            @PathVariable Long id,
            @Valid
            @RequestBody
            OrderRequest request) {

        return service.update(
            id,
            request
        );
    }

    @PatchMapping("/{id}/status")
    public OrderEntity changeStatus(
            @PathVariable Long id,
            @Valid
            @RequestBody
            OrderStatusRequest request) {

        return service.changeStatus(
            id,
            request.getStatus()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity
            .noContent()
            .build();
    }
}
