package com.ferretapp.dtos;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VentaDTO {

    private Integer idVenta;

    @NotNull(message = "El usuario es obligatorio")
    private Integer idUsuario;

    private String nombreUsuario;
    private Integer idCliente;
    private String nombreCliente;
    private LocalDateTime fechaVenta;
    private LocalDateTime fechaCreacion;
    private BigDecimal total;

    @NotEmpty(message = "La venta debe tener al menos un producto")
    private List<DetalleVentaDTO> detalles;
}