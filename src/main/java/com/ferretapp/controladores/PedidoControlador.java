package com.ferretapp.controladores;

import com.ferretapp.dtos.PedidoDTO;
import com.ferretapp.servicios.PedidoServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoControlador {

    private final PedidoServicio pedidoServicio;

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listar() {
        return ResponseEntity.ok(pedidoServicio.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(pedidoServicio.obtenerPorId(id));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoDTO>> porEstado(@PathVariable String estado) {
        return ResponseEntity.ok(pedidoServicio.listarPorEstado(estado));
    }

    @GetMapping("/proveedor/{idProveedor}")
    public ResponseEntity<List<PedidoDTO>> porProveedor(@PathVariable Integer idProveedor) {
        return ResponseEntity.ok(pedidoServicio.listarPorProveedor(idProveedor));
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> crear(@RequestBody PedidoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoServicio.crear(dto));
    }

    // Cambiar estado: Pendiente → Recibido | Cancelado
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoDTO> cambiarEstado(@PathVariable Integer id,
                                                   @RequestParam String estado) {
        return ResponseEntity.ok(pedidoServicio.cambiarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        pedidoServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}