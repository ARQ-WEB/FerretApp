package com.ferretapp.servicios;

import com.ferretapp.dtos.UsuarioDTO;
import com.ferretapp.entidades.Rol;
import com.ferretapp.entidades.Usuario;
import com.ferretapp.repositorios.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final RolServicio rolServicio;
    private final PasswordEncoder passwordEncoder;

    // ── Listar activos ───────────────────────────────────────
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarActivos() {
        return usuarioRepositorio.findByEliminadoFalse()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Obtener por ID ───────────────────────────────────────
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorId(Integer id) {
        return toDTO(buscarOFallar(id));
    }

    // ── Crear ────────────────────────────────────────────────
    @Transactional
    public UsuarioDTO crear(UsuarioDTO dto) {
        if (usuarioRepositorio.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con el email: " + dto.getEmail());
        }

        Rol rol = rolServicio.buscarOFallar(dto.getIdRol());

        Usuario usuario = Usuario.builder()
                .nombreCompleto(dto.getNombreCompleto().trim())
                .email(dto.getEmail().toLowerCase().trim())
                .contrasena(passwordEncoder.encode(dto.getContrasena()))
                .rol(rol)
                .build();

        return toDTO(usuarioRepositorio.save(usuario));
    }

    // ── Actualizar ───────────────────────────────────────────
    @Transactional
    public UsuarioDTO actualizar(Integer id, UsuarioDTO dto) {
        Usuario usuario = buscarOFallar(id);

        // Validar email duplicado solo si cambió
        boolean emailCambio = !dto.getEmail().equalsIgnoreCase(usuario.getEmail());
        if (emailCambio && usuarioRepositorio.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con el email: " + dto.getEmail());
        }

        Rol rol = rolServicio.buscarOFallar(dto.getIdRol());
        usuario.setNombreCompleto(dto.getNombreCompleto().trim());
        usuario.setEmail(dto.getEmail().toLowerCase().trim());
        usuario.setRol(rol);

        // Solo actualizar contraseña si viene en el DTO
        if (dto.getContrasena() != null && !dto.getContrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        }

        return toDTO(usuarioRepositorio.save(usuario));
    }

    // ── Eliminar lógico ──────────────────────────────────────
    @Transactional
    public void eliminar(Integer id) {
        Usuario usuario = buscarOFallar(id);
        // No permitir auto-eliminación
        usuario.setEliminado(true);
        usuarioRepositorio.save(usuario);
    }

    // ── Conteo de usuarios por rol ───────────────────────────
    @Transactional(readOnly = true)
    public java.util.Map<String, Long> conteoPorRol() {
        return usuarioRepositorio.findByEliminadoFalse().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        u -> u.getRol().getNombre(),
                        java.util.stream.Collectors.counting()
                ));
    }

    // ── Cambiar contraseña ───────────────────────────────────
    @Transactional
    public void cambiarPassword(Integer id, String nuevaPassword) {
        Usuario usuario = buscarOFallar(id);
        if (nuevaPassword == null || nuevaPassword.isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
        }
        usuario.setContrasena(passwordEncoder.encode(nuevaPassword));
        usuarioRepositorio.save(usuario);
    }

    // ── Cambiar estado (activar / desactivar) ────────────────
    @Transactional
    public UsuarioDTO cambiarEstado(Integer id, Boolean eliminado) {
        Usuario usuario = buscarOFallar(id);
        usuario.setEliminado(eliminado);
        return toDTO(usuarioRepositorio.save(usuario));
    }

    // ── Obtener por email ────────────────────────────────────
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorEmail(String email) {
        Usuario usuario = usuarioRepositorio
                .findByEmailIgnoreCaseAndEliminadoFalse(email)
                .orElseThrow(() -> new java.util.NoSuchElementException(
                        "Usuario no encontrado: " + email));
        return toDTO(usuario);
    }

    // ── Helpers ──────────────────────────────────────────────
    public Usuario buscarOFallar(Integer id) {
        return usuarioRepositorio.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Usuario no encontrado: " + id));
    }

    private UsuarioDTO toDTO(Usuario u) {
        return UsuarioDTO.builder()
                .idUsuario(u.getIdUsuario())
                .idRol(u.getRol().getIdRol())
                .nombreRol(u.getRol().getNombre())
                .nombreCompleto(u.getNombreCompleto())
                .email(u.getEmail())
                .eliminado(u.getEliminado())
                .build();
    }
}
