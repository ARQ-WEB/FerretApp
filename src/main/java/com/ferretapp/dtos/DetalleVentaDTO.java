package com.ferretapp.dtos;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DetalleVentaDTO {

    private Integer idDetalleVenta;

    @NotNull(message = "El producto es obligatorio")
    private Integer idProducto;

    private String nombreProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}