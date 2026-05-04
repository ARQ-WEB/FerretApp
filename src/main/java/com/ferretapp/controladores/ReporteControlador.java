package com.ferretapp.controladores;

import com.ferretapp.dtos.ProductoDTO;
import com.ferretapp.dtos.ReporteDTO;
import com.ferretapp.servicios.ReporteServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteControlador {

    private final ReporteServicio reporteServicio;

    @GetMapping("/ventas")
    public ResponseEntity<ReporteDTO> reporteVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime hasta) {
        return ResponseEntity.ok(reporteServicio.reporteVentas(desde, hasta));
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoDTO>> stockBajo() {
        return ResponseEntity.ok(reporteServicio.reporteStockBajo());
    }
}
