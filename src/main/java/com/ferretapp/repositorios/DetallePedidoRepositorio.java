package com.ferretapp.repositorios;

import com.ferretapp.entidades.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DetallePedidoRepositorio extends JpaRepository<DetallePedido, Integer> {

    List<DetallePedido> findByPedido_IdPedido(Integer idPedido);

    List<DetallePedido> findByProducto_IdProducto(Integer idProducto);

    // Total del pedido calculado en Java (equivalente a V_PEDIDO_TOTAL — 3FN)
    @Query("SELECT COALESCE(SUM(d.subtotal), 0) FROM DetallePedido d WHERE d.pedido.idPedido = :idPedido")
    BigDecimal calcularTotalPorPedido(Integer idPedido);
}