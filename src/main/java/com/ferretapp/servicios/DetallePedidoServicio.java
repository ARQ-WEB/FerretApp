package com.ferretapp.servicios;

import com.ferretapp.dtos.DetallePedidoDTO;
import com.ferretapp.entidades.DetallePedido;
import com.ferretapp.entidades.Pedido;
import com.ferretapp.entidades.Producto;
import com.ferretapp.repositorios.DetallePedidoRepositorio;
import com.ferretapp.repositorios.PedidoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DetallePedidoServicio {

    private final DetallePedidoRepositorio detallePedidoRepositorio;
    private final PedidoRepositorio pedidoRepositorio;
    private final ProductoServicio productoServicio;

    // ── Listar por pedido ────────────────────────────────────
    @Transactional(readOnly = true)
    public List<DetallePedidoDTO> listarPorPedido(Integer idPedido) {
        return detallePedidoRepositorio.findByPedido_IdPedido(idPedido)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Total del pedido ─────────────────────────────────────
    @Transactional(readOnly = true)
    public BigDecimal calcularTotal(Integer idPedido) {
        return detallePedidoRepositorio.calcularTotalPorPedido(idPedido);
    }

    // ── Agregar detalle ──────────────────────────────────────
    @Transactional
    public DetallePedidoDTO agregar(Integer idPedido, DetallePedidoDTO dto) {
        Pedido pedido = pedidoRepositorio.findById(idPedido)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado: " + idPedido));

        // 5FN: el proveedor del detalle debe coincidir con el del pedido
        Integer idProveedorPedido = pedido.getProveedor().getIdProveedor();
        if (!idProveedorPedido.equals(dto.getIdProveedor())) {
            throw new IllegalArgumentException(
                    "El proveedor del detalle (" + dto.getIdProveedor() +
                            ") no coincide con el del pedido (" + idProveedorPedido + ").");
        }

        Producto producto = productoServicio.buscarOFallar(dto.getIdProducto());

        DetallePedido detalle = DetallePedido.builder()
                .pedido(pedido)
                .producto(producto)
                .idProveedor(dto.getIdProveedor())
                .cantidad(dto.getCantidad())
                .precioUnitario(dto.getPrecioUnitario())
                .build();

        return toDTO(detallePedidoRepositorio.save(detalle));
    }

    // ── Eliminar detalle ─────────────────────────────────────
    @Transactional
    public void eliminar(Integer idDetalle) {
        if (!detallePedidoRepositorio.existsById(idDetalle)) {
            throw new NoSuchElementException("Detalle de pedido no encontrado: " + idDetalle);
        }
        detallePedidoRepositorio.deleteById(idDetalle);
    }

    // ── Helpers ──────────────────────────────────────────────
    private DetallePedidoDTO toDTO(DetallePedido d) {
        return DetallePedidoDTO.builder()
                .idDetallePedido(d.getIdDetallePedido())
                .idProducto(d.getProducto().getIdProducto())
                .nombreProducto(d.getProducto().getNombre())
                .idProveedor(d.getIdProveedor())
                .cantidad(d.getCantidad())
                .precioUnitario(d.getPrecioUnitario())
                .subtotal(d.getSubtotal())
                .build();
    }
}