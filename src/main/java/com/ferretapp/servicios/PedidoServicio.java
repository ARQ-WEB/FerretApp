package com.ferretapp.servicios;

import com.ferretapp.dtos.DetallePedidoDTO;
import com.ferretapp.dtos.PedidoDTO;
import com.ferretapp.entidades.DetallePedido;
import com.ferretapp.entidades.Pedido;
import com.ferretapp.entidades.Producto;
import com.ferretapp.entidades.Proveedor;
import com.ferretapp.entidades.Usuario;
import com.ferretapp.repositorios.DetallePedidoRepositorio;
import com.ferretapp.repositorios.PedidoRepositorio;
import com.ferretapp.repositorios.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServicio {

    private final PedidoRepositorio pedidoRepositorio;
    private final DetallePedidoRepositorio detallePedidoRepositorio;
    private final ProveedorServicio proveedorServicio;
    private final ProductoServicio productoServicio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final AuditoriaServicio auditoriaServicio;

    @Transactional(readOnly = true)
    public List<PedidoDTO> listarTodos() {
        return pedidoRepositorio.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> listarPorEstado(String estado) {
        return pedidoRepositorio.findByEstado(estado)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> listarPorProveedor(Integer idProveedor) {
        return pedidoRepositorio.findByProveedor_IdProveedor(idProveedor)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PedidoDTO obtenerPorId(Integer id) {
        return toDTO(buscarOFallar(id));
    }

    @Transactional
    public PedidoDTO crear(PedidoDTO dto) {
        Proveedor proveedor = proveedorServicio.buscarOFallar(dto.getIdProveedor());

        Pedido pedido = Pedido.builder()
                .proveedor(proveedor)
                .fechaPedido(dto.getFechaPedido() != null ? dto.getFechaPedido() : java.time.LocalDateTime.now())
                .fechaEntregaEsperada(dto.getFechaEntregaEsperada())
                .estado(dto.getEstado() != null ? dto.getEstado() : "Pendiente")
                .build();

        if (dto.getDetalles() != null && !dto.getDetalles().isEmpty()) {
            List<DetallePedido> detalles = new ArrayList<>();
            for (DetallePedidoDTO d : dto.getDetalles()) {
                if (!proveedor.getIdProveedor().equals(d.getIdProveedor())) {
                    throw new IllegalArgumentException(
                            "El proveedor del detalle (" + d.getIdProveedor() +
                                    ") no coincide con el del pedido (" + proveedor.getIdProveedor() + ").");
                }
                Producto producto = productoServicio.buscarOFallar(d.getIdProducto());
                BigDecimal subtotal = d.getPrecioUnitario()
                        .multiply(BigDecimal.valueOf(d.getCantidad()));
                detalles.add(DetallePedido.builder()
                        .pedido(pedido)
                        .producto(producto)
                        .idProveedor(d.getIdProveedor())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(subtotal)
                        .build());
            }
            pedido.setDetalles(detalles);
        }

        PedidoDTO resultado = toDTO(pedidoRepositorio.save(pedido));

        try {
            Integer idUsuario = getIdUsuarioActual();
            auditoriaServicio.registrar(idUsuario, "Pedido creado", "Pedido",
                    "Proveedor: " + proveedor.getNombreEmpresa() + " - Total: $" + resultado.getTotal());
        } catch (Exception ignored) {}

        return resultado;
    }

    @Transactional
    public PedidoDTO cambiarEstado(Integer id, String nuevoEstado) {
        List<String> estadosValidos = List.of("Pendiente", "Recibido", "Cancelado");
        if (!estadosValidos.contains(nuevoEstado)) {
            throw new IllegalArgumentException("Estado inválido: " + nuevoEstado);
        }
        Pedido pedido = buscarOFallar(id);

        if ("Recibido".equals(nuevoEstado) && !"Recibido".equals(pedido.getEstado())) {
            for (DetallePedido d : pedido.getDetalles()) {
                productoServicio.actualizarStock(d.getProducto().getIdProducto(), d.getCantidad());
            }
        }

        pedido.setEstado(nuevoEstado);

        try {
            Integer idUsuario = getIdUsuarioActual();
            auditoriaServicio.registrar(idUsuario, "Pedido " + nuevoEstado.toLowerCase(), "Pedido",
                    "Pedido #" + id + " marcado como " + nuevoEstado);
        } catch (Exception ignored) {}

        return toDTO(pedidoRepositorio.save(pedido));
    }

    @Transactional
    public void eliminar(Integer id) {
        Pedido pedido = buscarOFallar(id);
        if ("Recibido".equals(pedido.getEstado())) {
            throw new IllegalStateException("No se puede eliminar un pedido ya recibido.");
        }
        pedidoRepositorio.delete(pedido);
    }

    private Pedido buscarOFallar(Integer id) {
        return pedidoRepositorio.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado: " + id));
    }

    private Integer getIdUsuarioActual() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepositorio.findByEmailIgnoreCaseAndEliminadoFalse(email)
                .map(Usuario::getIdUsuario).orElse(1);
    }

    private PedidoDTO toDTO(Pedido p) {
        BigDecimal total = detallePedidoRepositorio.calcularTotalPorPedido(p.getIdPedido());

        List<DetallePedidoDTO> detallesDTO = p.getDetalles().stream().map(d ->
                DetallePedidoDTO.builder()
                        .idDetallePedido(d.getIdDetallePedido())
                        .idProducto(d.getProducto().getIdProducto())
                        .nombreProducto(d.getProducto().getNombre())
                        .idProveedor(d.getIdProveedor())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getSubtotal())
                        .build()
        ).collect(Collectors.toList());

        return PedidoDTO.builder()
                .idPedido(p.getIdPedido())
                .idProveedor(p.getProveedor().getIdProveedor())
                .nombreProveedor(p.getProveedor().getNombreEmpresa())
                .fechaPedido(p.getFechaPedido())
                .fechaEntregaEsperada(p.getFechaEntregaEsperada())
                .estado(p.getEstado())
                .total(total)
                .detalles(detallesDTO)
                .build();
    }
}