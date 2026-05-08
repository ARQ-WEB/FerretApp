package com.ferretapp.controladores;

import com.ferretapp.dtos.RolDTO;
import com.ferretapp.servicios.RolServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolControlador {

    private final RolServicio rolServicio;

    @GetMapping
    public ResponseEntity<List<RolDTO>> listar() {
        return ResponseEntity.ok(rolServicio.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(rolServicio.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<RolDTO> crear(@Valid @RequestBody RolDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rolServicio.crear(dto));
    }
}