package com.ferretapp.repositorios;

import com.ferretapp.entidades.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProveedorRepositorio extends JpaRepository<Proveedor, Integer> {

    List<Proveedor> findByEliminadoFalse();

    boolean existsByEmailIgnoreCase(String email);
}