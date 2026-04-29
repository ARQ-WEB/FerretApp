package com.ferretapp.controladores;

import com.ferretapp.dtos.ProductoDTO;
import com.ferretapp.dtos.ProveedorDTO;
import com.ferretapp.servicios.ProductoServicio;
import com.ferretapp.servicios.ProveedorServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // Productos que suministra un proveedor (4FN)
    @GetMapping("/{id}/productos")
    public ResponseEntity<List<ProductoDTO>> productos(@PathVariable Integer id) {
        return ResponseEntity.ok(productoServicio.listarPorProveedor(id));
    }

    @PostMapping
    public ResponseEntity<ProveedorDTO> crear(@RequestBody ProveedorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorServicio.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorDTO> actualizar(@PathVariable Integer id,
                                                   @RequestBody ProveedorDTO dto) {
        return ResponseEntity.ok(proveedorServicio.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        proveedorServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}