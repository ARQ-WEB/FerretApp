package com.ferretapp.servicios;

import com.ferretapp.dtos.AuditoriaDTO;
import com.ferretapp.entidades.Auditoria;
import com.ferretapp.entidades.Usuario;
import com.ferretapp.repositorios.AuditoriaRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditoriaServicio {

    private final AuditoriaRepositorio auditoriaRepositorio;
    private final UsuarioServicio usuarioServicio;

    // ── Registrar acción ─────────────────────────────────────
    @Transactional
    public void registrar(Integer idUsuario, String accion,
                          String entidad, String detalle) {
        Usuario usuario = usuarioServicio.buscarOFallar(idUsuario);
        Auditoria auditoria = Auditoria.builder()
                .usuario(usuario)
                .accion(accion)
                .entidad(entidad)
                .detalle(detalle)
                .build();
        auditoriaRepositorio.save(auditoria);
    }

    // ── Listar todos ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<AuditoriaDTO> listarTodos() {
        return auditoriaRepositorio.findAllByOrderByFechaHoraDesc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Filtrar por usuario ──────────────────────────────────
    @Transactional(readOnly = true)
    public List<AuditoriaDTO> listarPorUsuario(Integer idUsuario) {
        return auditoriaRepositorio.findByUsuario_IdUsuario(idUsuario)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Filtrar por acción ───────────────────────────────────
    @Transactional(readOnly = true)
    public List<AuditoriaDTO> listarPorAccion(String accion) {
        return auditoriaRepositorio.findByAccionIgnoreCase(accion)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Filtrar por entidad ──────────────────────────────────
    @Transactional(readOnly = true)
    public List<AuditoriaDTO> listarPorEntidad(String entidad) {
        return auditoriaRepositorio.findByEntidadIgnoreCase(entidad)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Filtrar por rango de fechas ──────────────────────────
    @Transactional(readOnly = true)
    public List<AuditoriaDTO> listarPorFechas(LocalDateTime desde,
                                              LocalDateTime hasta) {
        return auditoriaRepositorio
                .findByFechaHoraBetweenOrderByFechaHoraDesc(desde, hasta)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Resumen — cantidad de acciones por tipo ──────────────
    @Transactional(readOnly = true)
    public long contarPorAccion(String accion) {
        return auditoriaRepositorio.findByAccionIgnoreCase(accion).size();
    }

    // ── Helpers ──────────────────────────────────────────────
    private AuditoriaDTO toDTO(Auditoria a) {
        return AuditoriaDTO.builder()
                .idAuditoria(a.getIdAuditoria())
                .idUsuario(a.getUsuario().getIdUsuario())
                .nombreUsuario(a.getUsuario().getNombreCompleto())
                .accion(a.getAccion())
                .entidad(a.getEntidad())
                .detalle(a.getDetalle())
                .fechaHora(a.getFechaHora())
                .build();
    }
}
