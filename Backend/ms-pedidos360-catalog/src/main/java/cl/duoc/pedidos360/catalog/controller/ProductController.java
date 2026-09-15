package cl.duoc.pedidos360.catalog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.pedidos360.catalog.entity.ProductEntity;
import cl.duoc.pedidos360.catalog.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(
            ProductService service) {

        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ProductEntity>>
            findAll() {

        return ResponseEntity.ok(
            service.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductEntity>
            findById(
                @PathVariable Long id) {

        return ResponseEntity.ok(
            service.findById(id)
        );
    }

    @PostMapping
    public ResponseEntity<ProductEntity>
            create(
                @Valid
                @RequestBody ProductEntity product) {

        ProductEntity created =
            service.create(product);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductEntity>
            update(
                @PathVariable Long id,
                @Valid
                @RequestBody ProductEntity product) {

        return ResponseEntity.ok(
            service.update(id, product)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
            delete(
                @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}