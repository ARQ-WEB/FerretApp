package com.ferretapp.dtos;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DashboardDTO {

    // KPIs generales
    private Long totalProductos;
    private Integer stockTotal;
    private Long productosStockBajo;
    private Long ventasUltimos7Dias;
    private BigDecimal ingresosUltimos7Dias;

    // Alertas de stock bajo
    private List<ProductoDTO> alertasStockBajo;

    // Productos más vendidos
    private List<ReporteDTO.ProductoVendidoDTO> productosMasVendidos;
}
