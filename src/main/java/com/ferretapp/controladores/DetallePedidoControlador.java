package com.ferretapp.controladores;

import com.ferretapp.dtos.DetallePedidoDTO;
import com.ferretapp.servicios.DetallePedidoServicio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
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
        DetallePedidoDTO agregado = detallePedidoServicio.agregar(idPedido, dto);
        log.info("Detalle agregado al pedido {}: detalleId={}", idPedido, agregado.getIdDetallePedido());
        return ResponseEntity.status(HttpStatus.CREATED).body(agregado);
    }

    @DeleteMapping("/{idDetalle}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer idPedido,
                                         @PathVariable Integer idDetalle) {
        detallePedidoServicio.eliminar(idDetalle);
        log.info("Detalle eliminado del pedido {}: detalleId={}", idPedido, idDetalle);
        return ResponseEntity.noContent().build();
    }
}