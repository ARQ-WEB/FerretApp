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

    @Transactional(readOnly = true)
    public ReporteDTO reporteVentas(LocalDateTime desde, LocalDateTime hasta) {
        List<Venta> ventas = ventaRepositorio.findByFechaVentaBetween(desde, hasta);

        BigDecimal ingresosTotales = BigDecimal.ZERO;
        Integer unidadesVendidas = 0;

        Map<String, BigDecimal> ventasPorDia = new TreeMap<>();
        Map<String, BigDecimal> ventasPorCategoria = new HashMap<>();
        Map<String, Object[]> productosMap = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Venta venta : ventas) {
            String dia = venta.getFechaVenta().format(formatter);

            for (DetalleVenta detalle : venta.getDetalles()) {
                BigDecimal subtotal = detalle.getPrecioUnitario()
                        .multiply(BigDecimal.valueOf(detalle.getCantidad()));

                ingresosTotales = ingresosTotales.add(subtotal);
                unidadesVendidas += detalle.getCantidad();
                ventasPorDia.merge(dia, subtotal, BigDecimal::add);

                detalle.getProducto().getCategorias().forEach(cat -> {
                    ventasPorCategoria.merge(cat.getNombre(), subtotal, BigDecimal::add);
                });

                String nombreProducto = detalle.getProducto().getNombre();
                productosMap.computeIfAbsent(nombreProducto, k -> new Object[]{0, BigDecimal.ZERO});
                productosMap.get(nombreProducto)[0] = (int) productosMap.get(nombreProducto)[0] + detalle.getCantidad();
                productosMap.get(nombreProducto)[1] = ((BigDecimal) productosMap.get(nombreProducto)[1]).add(subtotal);
            }
        }

        BigDecimal ventaPromedio = ventas.isEmpty() ? BigDecimal.ZERO :
                ingresosTotales.divide(BigDecimal.valueOf(ventas.size()), 2, RoundingMode.HALF_UP);

        List<ReporteDTO.ProductoVendidoDTO> productosMasVendidos = productosMap
                .entrySet().stream()
                .sorted((a, b) -> (int) b.getValue()[0] - (int) a.getValue()[0])
                .limit(10)
                .map(e -> ReporteDTO.ProductoVendidoDTO.builder()
                        .nombreProducto(e.getKey())
                        .unidadesVendidas((int) e.getValue()[0])
                        .ingresoGenerado((BigDecimal) e.getValue()[1])
                        .build())
                .collect(Collectors.toList());

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

    @Transactional(readOnly = true)
    public List<ProductoDTO> reporteStockBajo() {
        return productoServicio.listarConStockBajo();
    }

    @Transactional(readOnly = true)
    public Map<String, BigDecimal> ventasPorDia(LocalDateTime desde, LocalDateTime hasta) {
        return reporteVentas(desde, hasta).getVentasPorDia();
    }

    @Transactional(readOnly = true)
    public Map<String, BigDecimal> ventasPorCategoria(LocalDateTime desde, LocalDateTime hasta) {
        return reporteVentas(desde, hasta).getVentasPorCategoria();
    }

    @Transactional(readOnly = true)
    public List<ReporteDTO.ProductoVendidoDTO> productosMasVendidos(LocalDateTime desde, LocalDateTime hasta) {
        return reporteVentas(desde, hasta).getProductosMasVendidos();
    }
}