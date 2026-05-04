package com.ferretapp.controladores;

import com.ferretapp.dtos.VentaDTO;
import com.ferretapp.servicios.VentaServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ventaServicio.crear(dto));
    }
}