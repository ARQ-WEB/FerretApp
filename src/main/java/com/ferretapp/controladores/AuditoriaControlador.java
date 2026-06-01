package com.ferretapp.controladores;

import com.ferretapp.dtos.AuditoriaDTO;
import com.ferretapp.servicios.AuditoriaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
public class AuditoriaControlador {

    private final AuditoriaServicio auditoriaServicio;

    @GetMapping
    public ResponseEntity<List<AuditoriaDTO>> listar() {
        return ResponseEntity.ok(auditoriaServicio.listarTodos());
    }

    // GET /api/auditoria/resumen
    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> resumen() {
        return ResponseEntity.ok(auditoriaServicio.resumen());
    }

    // GET /api/auditoria/buscar?q=Frank Vegas
    @GetMapping("/buscar")
    public ResponseEntity<List<AuditoriaDTO>> buscar(@RequestParam String q) {
        return ResponseEntity.ok(auditoriaServicio.buscar(q));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<AuditoriaDTO>> porUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(auditoriaServicio.listarPorUsuario(idUsuario));
    }

    @GetMapping("/accion/{accion}")
    public ResponseEntity<List<AuditoriaDTO>> porAccion(@PathVariable String accion) {
        return ResponseEntity.ok(auditoriaServicio.listarPorAccion(accion));
    }

    @GetMapping("/entidad/{entidad}")
    public ResponseEntity<List<AuditoriaDTO>> porEntidad(@PathVariable String entidad) {
        return ResponseEntity.ok(auditoriaServicio.listarPorEntidad(entidad));
    }

    @GetMapping("/fechas")
    public ResponseEntity<List<AuditoriaDTO>> porFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(auditoriaServicio.listarPorFechas(desde, hasta));
    }
}
