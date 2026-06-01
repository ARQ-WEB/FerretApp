package com.ferretapp.config;

import com.ferretapp.entidades.Rol;
import com.ferretapp.repositorios.RolRepositorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds essential reference data on every application startup.
 * Inserts are idempotent: a role is only created when it does not exist yet.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RolRepositorio rolRepositorio;

    @Override
    public void run(ApplicationArguments args) {
        seedRol("ADMIN");
        seedRol("VENDEDOR");
    }

    private void seedRol(String nombre) {
        if (!rolRepositorio.existsByNombreIgnoreCase(nombre)) {
            rolRepositorio.save(Rol.builder().nombre(nombre).build());
            log.info("Rol '{}' creado automáticamente.", nombre);
        } else {
            log.debug("Rol '{}' ya existe, se omite la inserción.", nombre);
        }
    }
}
