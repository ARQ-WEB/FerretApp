package com.ferretapp.controladores;

import com.ferretapp.dtos.ProductoDTO;
import com.ferretapp.dtos.ProveedorDTO;
import com.ferretapp.servicios.ProductoServicio;
import com.ferretapp.servicios.ProveedorServicio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorControlador {

    private final ProveedorServicio proveedorServicio;
    private final ProductoServicio productoServicio;

    @GetMapping
    public ResponseEntity<List<ProveedorDTO>> listar() {
        return ResponseEntity.ok(proveedorServicio.listarActivos());
    }

    @GetMapping("/todos")
    public ResponseEntity<List<ProveedorDTO>> listarTodos() {
        return ResponseEntity.ok(proveedorServicio.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(proveedorServicio.obtenerPorId(id));
    }

    @GetMapping("/{id}/productos")
    public ResponseEntity<List<ProductoDTO>> productos(@PathVariable Integer id) {
        return ResponseEntity.ok(productoServicio.listarPorProveedor(id));
    }

    @PostMapping
    public ResponseEntity<ProveedorDTO> crear(@RequestBody ProveedorDTO dto) {
        ProveedorDTO creado = proveedorServicio.crear(dto);
        log.info("Proveedor registrado exitosamente: id={}, empresa={}", creado.getIdProveedor(), creado.getNombreEmpresa());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorDTO> actualizar(@PathVariable Integer id,
                                                   @RequestBody ProveedorDTO dto) {
        ProveedorDTO actualizado = proveedorServicio.actualizar(id, dto);
        log.info("Proveedor actualizado: id={}", actualizado.getIdProveedor());
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        proveedorServicio.eliminar(id);
        log.info("Proveedor eliminado: id={}", id);
        return ResponseEntity.noContent().build();
    }
}