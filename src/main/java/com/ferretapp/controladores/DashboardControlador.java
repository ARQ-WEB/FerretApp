package com.ferretapp.controladores;

import com.ferretapp.dtos.DashboardDTO;
import com.ferretapp.dtos.ReporteDTO;
import com.ferretapp.servicios.DashboardServicio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardControlador {

    private final DashboardServicio dashboardServicio;

    // GET /api/dashboard/resumen
    @GetMapping("/resumen")
    public ResponseEntity<DashboardDTO> resumen() {
        log.debug("Solicitando resumen del dashboard");
        return ResponseEntity.ok(dashboardServicio.resumen());
    }

    // GET /api/dashboard/productos-mas-vendidos?dias=7
    @GetMapping("/productos-mas-vendidos")
    public ResponseEntity<List<ReporteDTO.ProductoVendidoDTO>> productosMasVendidos(
            @RequestParam(defaultValue = "7") int dias) {
        log.debug("Solicitando productos más vendidos últimos {} días", dias);
        return ResponseEntity.ok(dashboardServicio.productosMasVendidos(dias));
    }
}
