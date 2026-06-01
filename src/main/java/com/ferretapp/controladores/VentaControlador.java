package com.ferretapp.controladores;

import com.ferretapp.dtos.VentaDTO;
import com.ferretapp.servicios.VentaServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaControlador {

    private final VentaServicio ventaServicio;

    @GetMapping
    public ResponseEntity<List<VentaDTO>> listar() {
        return ResponseEntity.ok(ventaServicio.listarTodas());
    }

    @GetMapping("/recientes")
    public ResponseEntity<List<VentaDTO>> recientes() {
        return ResponseEntity.ok(ventaServicio.listarRecientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(ventaServicio.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<VentaDTO> crear(@Valid @RequestBody VentaDTO dto) {
        VentaDTO creada = ventaServicio.crear(dto);
        log.info("Venta registrada: id={}, usuario={}", creada.getIdVenta(), creada.getIdUsuario());
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    // GET /api/ventas/por-vendedor/{idUsuario}
    @GetMapping("/por-vendedor/{idUsuario}")
    public ResponseEntity<List<VentaDTO>> porVendedor(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(ventaServicio.listarPorVendedor(idUsuario));
    }

    // GET /api/ventas/por-fechas?desde=...&hasta=...
    @GetMapping("/por-fechas")
    public ResponseEntity<List<VentaDTO>> porFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(ventaServicio.listarPorFechas(desde, hasta));
    }

    // PATCH /api/ventas/{id}/anular
    @PatchMapping("/{id}/anular")
    public ResponseEntity<VentaDTO> anular(@PathVariable Integer id) {
        VentaDTO anulada = ventaServicio.anular(id);
        log.info("Venta anulada: id={}", id);
        return ResponseEntity.ok(anulada);
    }
}