package cl.duoc.pedidos360.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.pedidos360.catalog.entity.ProductEntity;

public interface ProductRepository
        extends JpaRepository<ProductEntity, Long> {
}