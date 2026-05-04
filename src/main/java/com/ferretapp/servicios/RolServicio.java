package com.ferretapp.servicios;

import com.ferretapp.dtos.RolDTO;
import com.ferretapp.entidades.Rol;
import com.ferretapp.repositorios.RolRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolServicio {

    private final RolRepositorio rolRepositorio;

    // ── Listar todos ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<RolDTO> listarTodos() {
        return rolRepositorio.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Obtener por ID ───────────────────────────────────────
    @Transactional(readOnly = true)
    public RolDTO obtenerPorId(Integer id) {
        return toDTO(buscarOFallar(id));
    }

    // ── Crear ────────────────────────────────────────────────
    @Transactional
    public RolDTO crear(RolDTO dto) {
        if (rolRepositorio.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new IllegalArgumentException(
                    "Ya existe un rol con el nombre: " + dto.getNombre());
        }
        Rol rol = Rol.builder()
                .nombre(dto.getNombre().toUpperCase().trim())
                .build();
        return toDTO(rolRepositorio.save(rol));
    }

    // ── Helpers ──────────────────────────────────────────────
    public Rol buscarOFallar(Integer id) {
        return rolRepositorio.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Rol no encontrado: " + id));
    }

    private RolDTO toDTO(Rol r) {
        return RolDTO.builder()
                .idRol(r.getIdRol())
                .nombre(r.getNombre())
                .build();
    }
}