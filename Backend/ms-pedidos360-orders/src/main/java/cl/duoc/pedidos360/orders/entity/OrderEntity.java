package cl.duoc.pedidos360.orders.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "PEDIDOS")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "CUSTOMER_ID",
        nullable = false,
        length = 150
    )
    private String customerId;

    @Column(
        name = "DESCRIPTION",
        nullable = false,
        length = 500
    )
    private String description;

    @Column(
        name = "TOTAL",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "STATUS",
        nullable = false,
        length = 30
    )
    private OrderStatus status;

    @Column(
        name = "CREATED_AT",
        nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
        name = "UPDATED_AT",
        nullable = false
    )
    private LocalDateTime updatedAt;

    public OrderEntity() {
    }

    @PrePersist
    public void prePersist() {

        LocalDateTime now =
            LocalDateTime.now();

        if (status == null) {
            status = OrderStatus.CREATED;
        }

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {

        updatedAt =
            LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(
            String customerId) {

        this.customerId =
            customerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {

        this.description =
            description;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(
            BigDecimal total) {

        this.total =
            total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(
            OrderStatus status) {

        this.status =
            status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt =
            createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {

        this.updatedAt =
            updatedAt;
    }
}