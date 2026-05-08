package com.ferretapp.dtos;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ReporteDTO {

    // KPIs generales
    private BigDecimal ingresosTotales;
    private Long ventasTotales;
    private Integer unidadesVendidas;
    private BigDecimal ventaPromedio;

    // Gráfico de ventas por día
    private Map<String, BigDecimal> ventasPorDia;

    // Gráfico de ventas por categoría
    private Map<String, BigDecimal> ventasPorCategoria;

    // Productos más vendidos
    private List<ProductoVendidoDTO> productosMasVendidos;

    // Stock bajo
    private List<ProductoDTO> productosStockBajo;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class ProductoVendidoDTO {
        private String nombreProducto;
        private Integer unidadesVendidas;
        private BigDecimal ingresoGenerado;
    }
}