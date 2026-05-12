package com.ferretapp.controladores;

import com.ferretapp.dtos.UsuarioDTO;
import com.ferretapp.servicios.UsuarioServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioControlador {

    private final UsuarioServicio usuarioServicio;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        return ResponseEntity.ok(usuarioServicio.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioServicio.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> crear(@Valid @RequestBody UsuarioDTO dto) {
        UsuarioDTO creado = usuarioServicio.crear(dto);
        log.info("Usuario registrado exitosamente: id={}, email={}", creado.getIdUsuario(), creado.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable Integer id,
                                                 @Valid @RequestBody UsuarioDTO dto) {
        UsuarioDTO actualizado = usuarioServicio.actualizar(id, dto);
        log.info("Usuario actualizado: id={}", id);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        usuarioServicio.eliminar(id);
        log.info("Usuario eliminado: id={}", id);
        return ResponseEntity.noContent().build();
    }
}