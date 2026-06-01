package com.ferretapp.servicios;

import com.ferretapp.dtos.DashboardDTO;
import com.ferretapp.dtos.ProductoDTO;
import com.ferretapp.dtos.ReporteDTO;
import com.ferretapp.entidades.Venta;
import com.ferretapp.repositorios.VentaRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServicio {

    private final ProductoServicio productoServicio;
    private final VentaRepositorio ventaRepositorio;
    private final ReporteServicio reporteServicio;

    // ── Resumen general del dashboard ───────────────────────────
    @Transactional(readOnly = true)
    public DashboardDTO resumen() {
        LocalDateTime hasta = LocalDateTime.now();
        LocalDateTime desde = hasta.minusDays(7);

        List<ProductoDTO> activos    = productoServicio.listarActivos();
        List<ProductoDTO> stockBajo  = productoServicio.listarConStockBajo();
        List<Venta>       ventas7    = ventaRepositorio.findByFechaVentaBetween(desde, hasta);

        int stockTotal = activos.stream()
                .mapToInt(p -> p.getStockActual() != null ? p.getStockActual() : 0)
                .sum();

        BigDecimal ingresos7 = ventas7.stream()
                .flatMap(v -> v.getDetalles().stream())
                .map(d -> d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Productos más vendidos últimos 7 días (top 5)
        ReporteDTO reporte = reporteServicio.reporteVentas(desde, hasta);
        List<ReporteDTO.ProductoVendidoDTO> masVendidos = reporte.getProductosMasVendidos()
                .stream().limit(5).toList();

        return DashboardDTO.builder()
                .totalProductos((long) activos.size())
                .stockTotal(stockTotal)
                .productosStockBajo((long) stockBajo.size())
                .ventasUltimos7Dias((long) ventas7.size())
                .ingresosUltimos7Dias(ingresos7)
                .alertasStockBajo(stockBajo)
                .productosMasVendidos(masVendidos)
                .build();
    }

    // ── Productos más vendidos por N días ────────────────────────
    @Transactional(readOnly = true)
    public List<ReporteDTO.ProductoVendidoDTO> productosMasVendidos(int dias) {
        LocalDateTime hasta = LocalDateTime.now();
        LocalDateTime desde = hasta.minusDays(dias);
        return reporteServicio.reporteVentas(desde, hasta).getProductosMasVendidos();
    }
}
