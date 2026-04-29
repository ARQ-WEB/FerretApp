package com.ferretapp.repositorios;

import com.ferretapp.entidades.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoriaRepositorio extends JpaRepository<Categoria, Integer> {

    // Sólo categorías activas
    List<Categoria> findByEliminadoFalse();

    boolean existsByNombreIgnoreCase(String nombre);
}