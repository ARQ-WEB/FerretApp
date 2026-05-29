package com.ferretapp.repositorios;

import com.ferretapp.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {

    List<Usuario> findByEliminadoFalse();

    Optional<Usuario> findByEmailIgnoreCaseAndEliminadoFalse(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Usuario> findByRol_IdRol(Integer idRol);
}
