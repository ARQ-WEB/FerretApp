package com.ferretapp.repositorios;

import com.ferretapp.entidades.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProveedorRepositorio extends JpaRepository<Proveedor, Integer> {

    List<Proveedor> findByEliminadoFalse();

    boolean existsByEmailIgnoreCase(String email);

    // Búsqueda por nombre de empresa, contacto o email
    @Query("SELECT p FROM Proveedor p WHERE p.eliminado = false AND (" +
           "LOWER(p.nombreEmpresa)   LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(p.nombreContacto) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(p.email)          LIKE LOWER(CONCAT('%', :q, '%')))")
    List<Proveedor> buscar(String q);
}
