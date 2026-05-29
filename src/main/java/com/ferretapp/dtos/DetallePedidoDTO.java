package com.ferretapp.dtos;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DetallePedidoDTO {

    private Integer idDetallePedido;
    private Integer idProducto;
    private String nombreProducto;
    private Integer idProveedor;   // 5FN: debe coincidir con proveedor del pedido
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;   // calculado en BD (GENERATED)
}