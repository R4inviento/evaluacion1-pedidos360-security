package cl.duoc.pedidos360.orders.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class OrderRequest {

    @NotBlank(message = "customerId es obligatorio")
    private String customerId;

    @NotBlank(message = "description es obligatoria")
    @Size(
        max = 500,
        message = "description no puede superar 500 caracteres"
    )
    private String description;

    @NotNull(message = "total es obligatorio")
    @DecimalMin(
        value = "0.01",
        message = "total debe ser mayor a 0"
    )
    private BigDecimal total;

    public OrderRequest() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
