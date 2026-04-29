package com.ferretapp.controladores;

import com.ferretapp.dtos.DetallePedidoDTO;
import com.ferretapp.servicios.DetallePedidoServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos/{idPedido}/detalles")
@RequiredArgsConstructor
public class DetallePedidoControlador {

    private final DetallePedidoServicio detallePedidoServicio;

    @GetMapping
    public ResponseEntity<List<DetallePedidoDTO>> listar(@PathVariable Integer idPedido) {
        return ResponseEntity.ok(detallePedidoServicio.listarPorPedido(idPedido));
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> total(@PathVariable Integer idPedido) {
        return ResponseEntity.ok(detallePedidoServicio.calcularTotal(idPedido));
    }

    @PostMapping
    public ResponseEntity<DetallePedidoDTO> agregar(@PathVariable Integer idPedido,
                                                    @RequestBody DetallePedidoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(detallePedidoServicio.agregar(idPedido, dto));
    }

    @DeleteMapping("/{idDetalle}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer idPedido,
                                         @PathVariable Integer idDetalle) {
        detallePedidoServicio.eliminar(idDetalle);
        return ResponseEntity.noContent().build();
    }
}