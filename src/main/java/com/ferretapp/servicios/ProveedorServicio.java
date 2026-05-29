package com.ferretapp.servicios;

import com.ferretapp.dtos.ProveedorDTO;
import com.ferretapp.entidades.Proveedor;
import com.ferretapp.repositorios.ProveedorRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProveedorServicio {

    private final ProveedorRepositorio proveedorRepositorio;

    // ── Listar ──────────────────────────────────────────────
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

    // ── Obtener por ID ───────────────────────────────────────
    @Transactional(readOnly = true)
    public ProveedorDTO obtenerPorId(Integer id) {
        return toDTO(buscarOFallar(id));
    }

    // ── Crear ────────────────────────────────────────────────
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
        return toDTO(proveedorRepositorio.save(proveedor));
    }

    // ── Actualizar ───────────────────────────────────────────
    @Transactional
    public ProveedorDTO actualizar(Integer id, ProveedorDTO dto) {
        Proveedor proveedor = buscarOFallar(id);
        proveedor.setNombreEmpresa(dto.getNombreEmpresa());
        proveedor.setNombreContacto(dto.getNombreContacto());
        proveedor.setEmail(dto.getEmail());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setDireccion(dto.getDireccion());
        return toDTO(proveedorRepositorio.save(proveedor));
    }

    // ── Eliminar lógico ──────────────────────────────────────
    @Transactional
    public void eliminar(Integer id) {
        Proveedor proveedor = buscarOFallar(id);
        proveedor.setEliminado(true);
        proveedorRepositorio.save(proveedor);
    }

    // ── Helpers ──────────────────────────────────────────────
    public Proveedor buscarOFallar(Integer id) {
        return proveedorRepositorio.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Proveedor no encontrado: " + id));
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