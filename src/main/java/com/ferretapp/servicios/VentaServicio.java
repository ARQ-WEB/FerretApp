package com.ferretapp.servicios;

import com.ferretapp.dtos.DetalleVentaDTO;
import com.ferretapp.dtos.VentaDTO;
import com.ferretapp.entidades.*;
import com.ferretapp.repositorios.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentaServicio {

    private final VentaRepositorio ventaRepositorio;
    private final DetalleVentaRepositorio detalleVentaRepositorio;
    private final ProductoServicio productoServicio;
    private final ClienteServicio clienteServicio;
    private final UsuarioServicio usuarioServicio;

    // ── Listar todos ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<VentaDTO> listarTodas() {
        return ventaRepositorio.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Últimas 5 ventas ─────────────────────────────────────
    @Transactional(readOnly = true)
    public List<VentaDTO> listarRecientes() {
        return ventaRepositorio.findTop5ByOrderByFechaVentaDesc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Obtener por ID ───────────────────────────────────────
    @Transactional(readOnly = true)
    public VentaDTO obtenerPorId(Integer id) {
        return toDTO(buscarOFallar(id));
    }

    // ── Crear venta ──────────────────────────────────────────
    @Transactional
    public VentaDTO crear(VentaDTO dto) {
        // Validar usuario
        Usuario usuario = usuarioServicio.buscarOFallar(dto.getIdUsuario());

        // Cliente es opcional
        Cliente cliente = null;
        if (dto.getIdCliente() != null) {
            cliente = clienteServicio.buscarOFallar(dto.getIdCliente());
        }

        Venta venta = Venta.builder()
                .usuario(usuario)
                .cliente(cliente)
                .build();

        // Construir detalles y descontar stock
        List<DetalleVenta> detalles = new ArrayList<>();
        for (DetalleVentaDTO d : dto.getDetalles()) {
            Producto producto = productoServicio.buscarOFallar(d.getIdProducto());

            // Validar stock disponible
            if (producto.getStockActual() < d.getCantidad()) {
                throw new IllegalStateException(
                        "Stock insuficiente para: " + producto.getNombre() +
                                ". Disponible: " + producto.getStockActual() +
                                ", solicitado: " + d.getCantidad());
            }

            // Descontar stock
            productoServicio.actualizarStock(producto.getIdProducto(), -d.getCantidad());

            // ← CAMBIO: calcular subtotal en Java
            BigDecimal subtotal = producto.getPrecioVenta()
                    .multiply(BigDecimal.valueOf(d.getCantidad()));

            detalles.add(DetalleVenta.builder()
                    .venta(venta)
                    .producto(producto)
                    .cantidad(d.getCantidad())
                    .precioUnitario(producto.getPrecioVenta())
                    .subtotal(subtotal)  // ← CAMBIO
                    .build());
        }

        venta.setDetalles(detalles);
        return toDTO(ventaRepositorio.save(venta));
    }

    // ── Helpers ──────────────────────────────────────────────
    public Venta buscarOFallar(Integer id) {
        return ventaRepositorio.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Venta no encontrada: " + id));
    }

    private VentaDTO toDTO(Venta v) {
        BigDecimal total = detalleVentaRepositorio.calcularTotalPorVenta(v.getIdVenta());

        List<DetalleVentaDTO> detallesDTO = v.getDetalles().stream().map(d ->
                DetalleVentaDTO.builder()
                        .idDetalleVenta(d.getIdDetalleVenta())
                        .idProducto(d.getProducto().getIdProducto())
                        .nombreProducto(d.getProducto().getNombre())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getSubtotal())
                        .build()
        ).collect(Collectors.toList());

        return VentaDTO.builder()
                .idVenta(v.getIdVenta())
                .idUsuario(v.getUsuario().getIdUsuario())
                .nombreUsuario(v.getUsuario().getNombreCompleto())
                .idCliente(v.getCliente() != null ? v.getCliente().getIdCliente() : null)
                .nombreCliente(v.getCliente() != null ? v.getCliente().getNombreCompleto() : null)
                .fechaVenta(v.getFechaVenta())
                .fechaCreacion(v.getFechaCreacion())
                .total(total)
                .detalles(detallesDTO)
                .build();
    }
}