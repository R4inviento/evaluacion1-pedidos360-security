package cl.duoc.pedidos360.bff.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/products")
public class CatalogProxyController {

    private final RestClient catalogRestClient;

    public CatalogProxyController(
            @Qualifier("catalogRestClient")
            RestClient catalogRestClient) {

        this.catalogRestClient =
            catalogRestClient;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> findAll() {

        return catalogRestClient
            .get()
            .uri("/api/products")
            .retrieve()
            .toEntity(String.class);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> findById(
            @PathVariable Long id) {

        return catalogRestClient
            .get()
            .uri("/api/products/{id}", id)
            .retrieve()
            .toEntity(String.class);
    }

    @PostMapping
    @PreAuthorize(
        "hasAnyRole('Admin', 'Operator')"
    )
    public ResponseEntity<String> create(
            @RequestBody
            Map<String, Object> body) {

        return catalogRestClient
            .post()
            .uri("/api/products")
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

        return catalogRestClient
            .put()
            .uri(
                "/api/products/{id}",
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

        catalogRestClient
            .delete()
            .uri(
                "/api/products/{id}",
                id
            )
            .retrieve()
            .toBodilessEntity();

        return ResponseEntity
            .noContent()
            .build();
    }
}