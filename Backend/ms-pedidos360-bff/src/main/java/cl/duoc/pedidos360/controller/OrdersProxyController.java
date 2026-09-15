package cl.duoc.pedidos360.bff.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/orders")
public class OrdersProxyController {

    private final RestClient ordersRestClient;

    public OrdersProxyController(
            RestClient ordersRestClient) {

        this.ordersRestClient =
            ordersRestClient;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> findAll() {

        return ordersRestClient
            .get()
            .uri("/api/orders")
            .retrieve()
            .toEntity(String.class);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> findById(
            @PathVariable Long id) {

        return ordersRestClient
            .get()
            .uri("/api/orders/{id}", id)
            .retrieve()
            .toEntity(String.class);
    }

    @PostMapping
    @PreAuthorize(
        "hasAuthority('SCOPE_OT.create')"
    )
    public ResponseEntity<String> create(
            @RequestBody
            Map<String, Object> body) {

        return ordersRestClient
            .post()
            .uri("/api/orders")
            .contentType(
                MediaType.APPLICATION_JSON
            )
            .body(body)
            .retrieve()
            .toEntity(String.class);
    }

    @PutMapping("/{id}")
    @PreAuthorize(
        "hasAnyRole('Admin', 'Operator')"
    )
    public ResponseEntity<String> update(
            @PathVariable Long id,
            @RequestBody
            Map<String, Object> body) {

        return ordersRestClient
            .put()
            .uri(
                "/api/orders/{id}",
                id
            )
            .contentType(
                MediaType.APPLICATION_JSON
            )
            .body(body)
            .retrieve()
            .toEntity(String.class);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize(
        "hasAnyRole('Admin', 'Operator')"
    )
    public ResponseEntity<String> changeStatus(
            @PathVariable Long id,
            @RequestBody
            Map<String, Object> body) {

        return ordersRestClient
            .patch()
            .uri(
                "/api/orders/{id}/status",
                id
            )
            .contentType(
                MediaType.APPLICATION_JSON
            )
            .body(body)
            .retrieve()
            .toEntity(String.class);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(
        "hasRole('Admin')"
    )
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        ordersRestClient
            .delete()
            .uri(
                "/api/orders/{id}",
                id
            )
            .retrieve()
            .toBodilessEntity();

        return ResponseEntity
            .noContent()
            .build();
    }
}