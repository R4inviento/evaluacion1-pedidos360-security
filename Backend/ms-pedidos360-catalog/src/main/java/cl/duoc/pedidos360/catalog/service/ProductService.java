package cl.duoc.pedidos360.catalog.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.pedidos360.catalog.entity.ProductEntity;
import cl.duoc.pedidos360.catalog.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(
            ProductRepository repository) {

        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ProductEntity> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public ProductEntity findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() ->
                new RuntimeException(
                    "Producto no encontrado con id " + id
                )
            );
    }

    @Transactional
    public ProductEntity create(
            ProductEntity product) {

        return repository.save(product);
    }

    @Transactional
    public ProductEntity update(
            Long id,
            ProductEntity data) {

        ProductEntity product =
            findById(id);

        product.setName(
            data.getName()
        );

        product.setDescription(
            data.getDescription()
        );

        product.setPrice(
            data.getPrice()
        );

        product.setStock(
            data.getStock()
        );

        product.setActive(
            data.getActive()
        );

        return repository.save(product);
    }

    @Transactional
    public void delete(Long id) {

        ProductEntity product =
            findById(id);

        repository.delete(product);
    }
}