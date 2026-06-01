package com.ferretapp.repositorios;

import com.ferretapp.entidades.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepositorio extends JpaRepository<Auditoria, Integer> {

    List<Auditoria> findByUsuario_IdUsuario(Integer idUsuario);

    List<Auditoria> findByAccionIgnoreCase(String accion);

    List<Auditoria> findByEntidadIgnoreCase(String entidad);

    List<Auditoria> findByFechaHoraBetweenOrderByFechaHoraDesc(
            LocalDateTime desde, LocalDateTime hasta);

    List<Auditoria> findAllByOrderByFechaHoraDesc();

    // Búsqueda por texto libre en usuario, acción o detalle
    @Query("SELECT a FROM Auditoria a WHERE " +
           "LOWER(a.usuario.nombreCompleto) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(a.accion)                 LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(a.detalle)                LIKE LOWER(CONCAT('%', :q, '%')) " +
           "ORDER BY a.fechaHora DESC")
    List<Auditoria> buscarPorTexto(String q);

    // Conteo por acción para resumen
    long countByAccionIgnoreCase(String accion);

    // Usuarios distintos con actividad hoy
    @Query("SELECT COUNT(DISTINCT a.usuario.idUsuario) FROM Auditoria a " +
           "WHERE a.fechaHora >= :inicio")
    long contarUsuariosActivosDesde(LocalDateTime inicio);
}
