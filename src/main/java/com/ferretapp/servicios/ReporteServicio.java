package com.ferretapp.servicios;

import com.ferretapp.dtos.ProductoDTO;
import com.ferretapp.dtos.ReporteDTO;
import com.ferretapp.entidades.DetalleVenta;
import com.ferretapp.entidades.Venta;
import com.ferretapp.repositorios.DetalleVentaRepositorio;
import com.ferretapp.repositorios.VentaRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteServicio {

    private final VentaRepositorio ventaRepositorio;
    private final DetalleVentaRepositorio detalleVentaRepositorio;
    private final ProductoServicio productoServicio;

    // ── Reporte de ventas por rango de fechas ────────────────
    @Transactional(readOnly = true)
    public ReporteDTO reporteVentas(LocalDateTime desde, LocalDateTime hasta) {
        List<Venta> ventas = ventaRepositorio.findByFechaVentaBetween(desde, hasta);

        // KPIs
        BigDecimal ingresosTotales = BigDecimal.ZERO;
        Integer unidadesVendidas = 0;

        // Ventas por día
        Map<String, BigDecimal> ventasPorDia = new TreeMap<>();

        // Ventas por categoría
        Map<String, BigDecimal> ventasPorCategoria = new HashMap<>();

        // Productos más vendidos
        Map<String, int[]> productosMap = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Venta venta : ventas) {
            String dia = venta.getFechaVenta().format(formatter);

            for (DetalleVenta detalle : venta.getDetalles()) {
                BigDecimal subtotal = detalle.getPrecioUnitario()
                        .multiply(BigDecimal.valueOf(detalle.getCantidad()));

                // Ingresos totales
                ingresosTotales = ingresosTotales.add(subtotal);

                // Unidades vendidas
                unidadesVendidas += detalle.getCantidad();

                // Ventas por día
                ventasPorDia.merge(dia, subtotal, BigDecimal::add);

                // Ventas por categoría
                detalle.getProducto().getCategorias().forEach(cat -> {
                    ventasPorCategoria.merge(cat.getNombre(), subtotal, BigDecimal::add);
                });

                // Productos más vendidos
                String nombreProducto = detalle.getProducto().getNombre();
                productosMap.computeIfAbsent(nombreProducto, k -> new int[]{0});
                productosMap.get(nombreProducto)[0] += detalle.getCantidad();
            }
        }

        // Venta promedio
        BigDecimal ventaPromedio = ventas.isEmpty() ? BigDecimal.ZERO :
                ingresosTotales.divide(
                        BigDecimal.valueOf(ventas.size()), 2, RoundingMode.HALF_UP);

        // Top productos más vendidos
        List<ReporteDTO.ProductoVendidoDTO> productosMasVendidos = productosMap
                .entrySet().stream()
                .sorted((a, b) -> b.getValue()[0] - a.getValue()[0])
                .limit(10)
                .map(e -> ReporteDTO.ProductoVendidoDTO.builder()
                        .nombreProducto(e.getKey())
                        .unidadesVendidas(e.getValue()[0])
                        .ingresoGenerado(BigDecimal.ZERO)
                        .build())
                .collect(Collectors.toList());

        // Stock bajo
        List<ProductoDTO> stockBajo = productoServicio.listarConStockBajo();

        return ReporteDTO.builder()
                .ingresosTotales(ingresosTotales)
                .ventasTotales((long) ventas.size())
                .unidadesVendidas(unidadesVendidas)
                .ventaPromedio(ventaPromedio)
                .ventasPorDia(ventasPorDia)
                .ventasPorCategoria(ventasPorCategoria)
                .productosMasVendidos(productosMasVendidos)
                .productosStockBajo(stockBajo)
                .build();
    }

    // ── Reporte de stock bajo ────────────────────────────────
    @Transactional(readOnly = true)
    public List<ProductoDTO> reporteStockBajo() {
        return productoServicio.listarConStockBajo();
    }
}