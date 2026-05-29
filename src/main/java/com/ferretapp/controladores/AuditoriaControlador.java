package com.ferretapp.controladores;

import com.ferretapp.dtos.AuditoriaDTO;
import com.ferretapp.servicios.AuditoriaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
public class AuditoriaControlador {

    private final AuditoriaServicio auditoriaServicio;

    @GetMapping
    public ResponseEntity<List<AuditoriaDTO>> listar() {
        return ResponseEntity.ok(auditoriaServicio.listarTodos());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<AuditoriaDTO>> porUsuario(
            @PathVariable Integer idUsuario) {
        return ResponseEntity.ok(auditoriaServicio.listarPorUsuario(idUsuario));
    }

    @GetMapping("/accion/{accion}")
    public ResponseEntity<List<AuditoriaDTO>> porAccion(
            @PathVariable String accion) {
        return ResponseEntity.ok(auditoriaServicio.listarPorAccion(accion));
    }

    @GetMapping("/entidad/{entidad}")
    public ResponseEntity<List<AuditoriaDTO>> porEntidad(
            @PathVariable String entidad) {
        return ResponseEntity.ok(auditoriaServicio.listarPorEntidad(entidad));
    }

    @GetMapping("/fechas")
    public ResponseEntity<List<AuditoriaDTO>> porFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime hasta) {
        return ResponseEntity.ok(auditoriaServicio.listarPorFechas(desde, hasta));
    }
}