package com.ferretapp.repositorios;

import com.ferretapp.entidades.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepositorio extends JpaRepository<Venta, Integer> {

    List<Venta> findByUsuario_IdUsuario(Integer idUsuario);

    List<Venta> findByFechaVentaBetween(LocalDateTime desde, LocalDateTime hasta);

    List<Venta> findTop5ByOrderByFechaVentaDesc();
}
