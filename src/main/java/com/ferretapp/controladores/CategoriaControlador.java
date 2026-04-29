package com.ferretapp.controladores;

import com.ferretapp.dtos.CategoriaDTO;
import com.ferretapp.servicios.CategoriaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaControlador {

    private final CategoriaServicio categoriaServicio;

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listar() {
        return ResponseEntity.ok(categoriaServicio.listarActivas());
    }

    @GetMapping("/todas")
    public ResponseEntity<List<CategoriaDTO>> listarTodas() {
        return ResponseEntity.ok(categoriaServicio.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(categoriaServicio.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<CategoriaDTO> crear(@RequestBody CategoriaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaServicio.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> actualizar(@PathVariable Integer id,
                                                   @RequestBody CategoriaDTO dto) {
        return ResponseEntity.ok(categoriaServicio.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        categoriaServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}