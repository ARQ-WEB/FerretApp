package com.ferretapp.controladores;

import com.ferretapp.dtos.ProductoDTO;
import com.ferretapp.dtos.ReporteDTO;
import com.ferretapp.servicios.ReporteServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteControlador {

    private final ReporteServicio reporteServicio;

    // GET /api/reportes/ventas?desde=...&hasta=...
    @GetMapping("/ventas")
    public ResponseEntity<ReporteDTO> reporteVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(reporteServicio.reporteVentas(desde, hasta));
    }

    // GET /api/reportes/stock-bajo
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoDTO>> stockBajo() {
        return ResponseEntity.ok(reporteServicio.reporteStockBajo());
    }

    // GET /api/reportes/ventas-por-dia?desde=...&hasta=...
    @GetMapping("/ventas-por-dia")
    public ResponseEntity<Map<String, BigDecimal>> ventasPorDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(reporteServicio.ventasPorDia(desde, hasta));
    }

    // GET /api/reportes/ventas-por-categoria?desde=...&hasta=...
    @GetMapping("/ventas-por-categoria")
    public ResponseEntity<Map<String, BigDecimal>> ventasPorCategoria(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(reporteServicio.ventasPorCategoria(desde, hasta));
    }

    // GET /api/reportes/productos-mas-vendidos?desde=...&hasta=...
    @GetMapping("/productos-mas-vendidos")
    public ResponseEntity<List<ReporteDTO.ProductoVendidoDTO>> productosMasVendidos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(reporteServicio.productosMasVendidos(desde, hasta));
    }
}
