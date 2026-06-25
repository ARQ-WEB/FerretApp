package com.ferretapp.servicios;

import com.ferretapp.dtos.ProveedorDTO;
import com.ferretapp.entidades.Proveedor;
import com.ferretapp.entidades.Usuario;
import com.ferretapp.repositorios.ProveedorRepositorio;
import com.ferretapp.repositorios.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProveedorServicio {

    private final ProveedorRepositorio proveedorRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final AuditoriaServicio auditoriaServicio;

    @Transactional(readOnly = true)
    public List<ProveedorDTO> listarActivos() {
        return proveedorRepositorio.findByEliminadoFalse()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProveedorDTO> listarTodos() {
        return proveedorRepositorio.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProveedorDTO obtenerPorId(Integer id) {
        return toDTO(buscarOFallar(id));
    }

    @Transactional
    public ProveedorDTO crear(ProveedorDTO dto) {
        if (proveedorRepositorio.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new IllegalArgumentException("Ya existe un proveedor con ese email: " + dto.getEmail());
        }
        Proveedor proveedor = Proveedor.builder()
                .nombreEmpresa(dto.getNombreEmpresa())
                .nombreContacto(dto.getNombreContacto())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .build();
        ProveedorDTO resultado = toDTO(proveedorRepositorio.save(proveedor));
        try {
            Integer idUsuario = getIdUsuarioActual();
            auditoriaServicio.registrar(idUsuario, "Proveedor creado", "Proveedor",
                    "Proveedor: " + dto.getNombreEmpresa());
        } catch (Exception ignored) {}
        return resultado;
    }

    @Transactional
    public ProveedorDTO actualizar(Integer id, ProveedorDTO dto) {
        Proveedor proveedor = buscarOFallar(id);
        proveedor.setNombreEmpresa(dto.getNombreEmpresa());
        proveedor.setNombreContacto(dto.getNombreContacto());
        proveedor.setEmail(dto.getEmail());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setDireccion(dto.getDireccion());
        ProveedorDTO resultado = toDTO(proveedorRepositorio.save(proveedor));
        try {
            Integer idUsuario = getIdUsuarioActual();
            auditoriaServicio.registrar(idUsuario, "Proveedor actualizado", "Proveedor",
                    "Proveedor: " + proveedor.getNombreEmpresa());
        } catch (Exception ignored) {}
        return resultado;
    }

    @Transactional
    public void eliminar(Integer id) {
        Proveedor proveedor = buscarOFallar(id);
        proveedor.setEliminado(true);
        proveedorRepositorio.save(proveedor);
        try {
            Integer idUsuario = getIdUsuarioActual();
            auditoriaServicio.registrar(idUsuario, "Proveedor eliminado", "Proveedor",
                    "Proveedor: " + proveedor.getNombreEmpresa());
        } catch (Exception ignored) {}
    }

    @Transactional(readOnly = true)
    public List<ProveedorDTO> buscar(String q) {
        return proveedorRepositorio.buscar(q)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long conteoProductos(Integer id) {
        Proveedor proveedor = buscarOFallar(id);
        return proveedor.getProductos().stream()
                .filter(p -> !Boolean.TRUE.equals(p.getEliminado()))
                .count();
    }

    public Proveedor buscarOFallar(Integer id) {
        return proveedorRepositorio.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Proveedor no encontrado: " + id));
    }

    private Integer getIdUsuarioActual() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepositorio.findByEmailIgnoreCaseAndEliminadoFalse(email)
                .map(Usuario::getIdUsuario).orElse(1);
    }

    private ProveedorDTO toDTO(Proveedor p) {
        return ProveedorDTO.builder()
                .idProveedor(p.getIdProveedor())
                .nombreEmpresa(p.getNombreEmpresa())
                .nombreContacto(p.getNombreContacto())
                .email(p.getEmail())
                .telefono(p.getTelefono())
                .direccion(p.getDireccion())
                .eliminado(p.getEliminado())
                .build();
    }
}