package com.ferretapp.repositorios;

import com.ferretapp.entidades.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, Integer> {

    List<Cliente> findByEliminadoFalse();

    Optional<Cliente> findByEmailIgnoreCaseAndEliminadoFalse(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Cliente> findByNombreCompletoContainingIgnoreCaseAndEliminadoFalse(String nombre);
}