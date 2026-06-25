package com.ferretapp.controladores;

import com.ferretapp.dtos.DetalleVentaDTO;
import com.ferretapp.repositorios.DetalleVentaRepositorio;
import com.ferretapp.entidades.DetalleVenta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/ventas/{idVenta}/detalles")
@RequiredArgsConstructor
public class DetalleVentaControlador {

    private final DetalleVentaRepositorio detalleVentaRepositorio;

    @GetMapping
    public ResponseEntity<List<DetalleVentaDTO>> listar(@PathVariable Integer idVenta) {
        List<DetalleVentaDTO> detalles = detalleVentaRepositorio
                .findByVenta_IdVenta(idVenta)
                .stream()
                .map(d -> DetalleVentaDTO.builder()
                        .idDetalleVenta(d.getIdDetalleVenta())
                        .idProducto(d.getProducto().getIdProducto())
                        .nombreProducto(d.getProducto().getNombre())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getSubtotal())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(detalles);
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> total(@PathVariable Integer idVenta) {
        BigDecimal total = detalleVentaRepositorio.calcularTotalPorVenta(idVenta);
        return ResponseEntity.ok(total);
    }
}
