package com.ferretapp.controladores;

import com.ferretapp.dtos.ProductoDTO;
import com.ferretapp.servicios.ProductoServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoControlador {

    private final ProductoServicio productoServicio;

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar() {
        return ResponseEntity.ok(productoServicio.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(productoServicio.obtenerPorId(id));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductoDTO> obtenerPorSku(@PathVariable String sku) {
        return ResponseEntity.ok(productoServicio.obtenerPorSku(sku));
    }

    // Alertas de stock bajo
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoDTO>> stockBajo() {
        return ResponseEntity.ok(productoServicio.listarConStockBajo());
    }

    // Filtrar por categoría (4FN)
    @GetMapping("/por-categoria/{idCategoria}")
    public ResponseEntity<List<ProductoDTO>> porCategoria(@PathVariable Integer idCategoria) {
        return ResponseEntity.ok(productoServicio.listarPorCategoria(idCategoria));
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@RequestBody ProductoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoServicio.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Integer id,
                                                  @RequestBody ProductoDTO dto) {
        return ResponseEntity.ok(productoServicio.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        productoServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}