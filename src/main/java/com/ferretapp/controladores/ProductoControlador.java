package com.ferretapp.controladores;

import com.ferretapp.dtos.ProductoDTO;
import com.ferretapp.servicios.ProductoServicio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoDTO>> stockBajo() {
        return ResponseEntity.ok(productoServicio.listarConStockBajo());
    }

    @GetMapping("/por-categoria/{idCategoria}")
    public ResponseEntity<List<ProductoDTO>> porCategoria(@PathVariable Integer idCategoria) {
        return ResponseEntity.ok(productoServicio.listarPorCategoria(idCategoria));
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@RequestBody ProductoDTO dto) {
        ProductoDTO creado = productoServicio.crear(dto);
        log.info("Producto registrado exitosamente: id={}, sku={}", creado.getIdProducto(), creado.getSku());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Integer id,
                                                  @RequestBody ProductoDTO dto) {
        ProductoDTO actualizado = productoServicio.actualizar(id, dto);
        log.info("Producto actualizado: id={}", id);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        productoServicio.eliminar(id);
        log.info("Producto eliminado: id={}", id);
        return ResponseEntity.noContent().build();
    }
}