package com.ferretapp.controladores;

import com.ferretapp.dtos.CategoriaDTO;
import com.ferretapp.servicios.CategoriaServicio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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
        CategoriaDTO creada = categoriaServicio.crear(dto);
        log.info("Categoría registrada exitosamente: id={}, nombre={}", creada.getIdCategoria(), creada.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> actualizar(@PathVariable Integer id,
                                                   @RequestBody CategoriaDTO dto) {
        CategoriaDTO actualizada = categoriaServicio.actualizar(id, dto);
        log.info("Categoría actualizada: id={}", id);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        categoriaServicio.eliminar(id);
        log.info("Categoría eliminada: id={}", id);
        return ResponseEntity.noContent().build();
    }
}