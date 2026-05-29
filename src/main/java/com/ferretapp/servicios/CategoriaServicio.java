package com.ferretapp.servicios;

import com.ferretapp.dtos.CategoriaDTO;
import com.ferretapp.entidades.Categoria;
import com.ferretapp.repositorios.CategoriaRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaServicio {

    private final CategoriaRepositorio categoriaRepositorio;

    // ── Listar ──────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarActivas() {
        return categoriaRepositorio.findByEliminadoFalse()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarTodas() {
        return categoriaRepositorio.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Obtener por ID ───────────────────────────────────────
    @Transactional(readOnly = true)
    public CategoriaDTO obtenerPorId(Integer id) {
        return toDTO(buscarOFallar(id));
    }

    // ── Crear ────────────────────────────────────────────────
    @Transactional
    public CategoriaDTO crear(CategoriaDTO dto) {
        if (categoriaRepositorio.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre: " + dto.getNombre());
        }
        Categoria categoria = Categoria.builder()
                .nombre(dto.getNombre())
                .build();
        return toDTO(categoriaRepositorio.save(categoria));
    }

    // ── Actualizar ───────────────────────────────────────────
    @Transactional
    public CategoriaDTO actualizar(Integer id, CategoriaDTO dto) {
        Categoria categoria = buscarOFallar(id);
        categoria.setNombre(dto.getNombre());
        return toDTO(categoriaRepositorio.save(categoria));
    }

    // ── Eliminar lógico ──────────────────────────────────────
    @Transactional
    public void eliminar(Integer id) {
        Categoria categoria = buscarOFallar(id);
        categoria.setEliminado(true);
        categoriaRepositorio.save(categoria);
    }

    // ── Helpers ──────────────────────────────────────────────
    private Categoria buscarOFallar(Integer id) {
        return categoriaRepositorio.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada: " + id));
    }

    private CategoriaDTO toDTO(Categoria c) {
        return CategoriaDTO.builder()
                .idCategoria(c.getIdCategoria())
                .nombre(c.getNombre())
                .eliminado(c.getEliminado())
                .build();
    }
}