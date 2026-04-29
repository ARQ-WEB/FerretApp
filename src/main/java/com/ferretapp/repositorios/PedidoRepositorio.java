package com.ferretapp.repositorios;

import com.ferretapp.entidades.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepositorio extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByProveedor_IdProveedor(Integer idProveedor);

    List<Pedido> findByEstado(String estado);

    List<Pedido> findByProveedor_IdProveedorAndEstado(Integer idProveedor, String estado);
}