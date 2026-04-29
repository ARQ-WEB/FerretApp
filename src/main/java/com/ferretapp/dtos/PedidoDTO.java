package com.ferretapp.dtos;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PedidoDTO {

    private Integer idPedido;
    private Integer idProveedor;
    private String nombreProveedor;
    private LocalDateTime fechaPedido;
    private LocalDateTime fechaEntregaEsperada;
    private String estado;          // Pendiente | Recibido | Cancelado
    private BigDecimal total;       // calculado sumando subtotales (3FN)
    private List<DetallePedidoDTO> detalles;
}