package com.ferretapp.repositorios;

import com.ferretapp.entidades.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
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
}